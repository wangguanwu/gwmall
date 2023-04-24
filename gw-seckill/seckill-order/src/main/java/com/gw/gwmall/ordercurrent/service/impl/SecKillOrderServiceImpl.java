package com.gw.gwmall.ordercurrent.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.gw.gwmall.common.util.StringUtils;
import com.gw.gwmall.domain.FlashPromotionProduct;
import com.gw.gwmall.ordercurrent.component.LocalCache;
import com.gw.gwmall.ordercurrent.constant.OrderConstant;
import com.gw.gwmall.ordercurrent.feignapi.promotion.PromotionFeignApi;
import com.gw.gwmall.ordercurrent.mapper.OmsOrderCancelMessageHistoryMapper;
import com.gw.gwmall.ordercurrent.model.*;
import com.gw.gwmall.ordercurrent.service.SecKillOrderService;
import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.constant.RedisKeyPrefixConst;
import com.gw.gwmall.common.exception.BusinessException;
import com.gw.gwmall.ordercurrent.component.rocketmq.OrderMessageSender;
import com.gw.gwmall.ordercurrent.domain.*;
import com.gw.gwmall.ordercurrent.mapper.OmsOrderItemMapper;
import com.gw.gwmall.ordercurrent.mapper.OmsOrderMapper;
import com.gw.gwmall.ordercurrent.util.RocksDBUtil;
import com.gw.gwmall.rediscomm.util.RedisCommonUtil;
import com.gw.gwmall.rediscomm.util.RedisSingleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.transaction.annotation.ShardingTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.rocksdb.RocksDBException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description: 秒杀订单处理
 **/
@Slf4j
@Service
public class SecKillOrderServiceImpl implements SecKillOrderService {

    @Resource
    private RedisSingleUtil redisStockUtil;

    @Resource
    private OmsOrderMapper orderMapper;

    @Resource
    private OmsOrderItemMapper orderItemMapper;

    @Resource
    private LocalCache<Boolean> cache;

    @Resource
    private OrderMessageSender orderMessageSender;

    @Resource
    private PromotionFeignApi promotionFeignApi;

    @Resource
    private Cache<String, FlashPromotionProduct> secKillCache;

    @Resource
    private OmsOrderCancelMessageHistoryMapper omsOrderCancelMessageHistoryMapper;

    @Resource
    private RedisTemplate<String, Object> redisSingleTemplate;

    private final AtomicBoolean isLoadScript = new AtomicBoolean(false);
    private final DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();

    private static final String PRE_DECR_STOCK = "  -- -------------------Lua脚本代码开始***************************\n" +
            "            -- 调用Redis的get指令，查询活动库存，其中KEYS[1]为传入的参数1，即库存key\n" +
            "            local c_s = redis.call('get', KEYS[1])\n" +
            "            -- 判断活动库存是否充足，其中KEYS[2]为传入的参数2，即当前抢购数量\n" +
            "            if not c_s or tonumber(c_s) < tonumber(ARGV[1]) then\n" +
            "               return 0\n" +
            "            end\n" +
            "            -- 如果活动库存充足，则进行扣减操作。其中KEYS[2]为传入的参数2，即当前抢购数量\n" +
            "            return redis.call('decrby',KEYS[1], ARGV[1])\n" +
            "            -- -------------------Lua脚本代码结束***********************";

    private static final String DEC_STOCK_NAME = "扣减库存脚本";

    /*fixme 此处从Redis缓存中获得订单状态，可以应对更高并发，订单的保存状态在Redis中有记录：
    * key ：RedisKeyPrefixConst.MIAOSHA_ASYNC_WAITING_PREFIX + memberId
                    + ":" + productId
     而且 消息消费者在数据库中生成订单后，修改了这个状态，参见：AscynCreateOrderReciever*/
    public CommonResult<?> checkOrder(Long orderId){
        if(null != orderMapper.selectByPrimaryKey(orderId)){
            return CommonResult.success("success");
        }else{
            return CommonResult.doing();
        }
    }

    @PostConstruct
    public void preloadScript() {
        if (isLoadScript.get()) {
            return;
        }
        redisScript.setScriptText(PRE_DECR_STOCK);
        redisScript.setResultType(Long.class);
        Objects.requireNonNull(redisSingleTemplate.getConnectionFactory()).getConnection()
                .scriptLoad(PRE_DECR_STOCK.getBytes(StandardCharsets.UTF_8));
        RedisCommonUtil.loadRedisScript(redisSingleTemplate, redisScript, DEC_STOCK_NAME);
        isLoadScript.set(true);
    }



    /**
     * 秒杀订单下单，采用Redis缓存中直接扣减库存，
     * MQ异步保存到DB下单模式，应对高并发进行削峰，
     * 如果发送消息到MQ也成为性能瓶颈，可以引入线程池，将消息改为异步发送
     * 但存在着Redis宕机和本服务同时宕机的可能，会造成数据的丢失，
     * 需要快速持久化扣减记录，采用WAL机制实现，保存到本地RockDB数据库
     */
    @Override
    public CommonResult<Map<String,Object>> generateSecKillOrder(SecKillOrderParam secKillOrderParam, Long memberId,
                                                                 String token,Integer stockCount) throws BusinessException {
        Long productId = secKillOrderParam.getProductId();
        CommonResult commonResult = confirmCheck(productId,memberId,token);
        if(commonResult.getCode() == 500){
            return commonResult;
        }
        Long orderId = secKillOrderParam.getOrderId();
        Long orderItemId = secKillOrderParam.getOrderItemId();
        if(null == orderId || null == orderItemId){
            throw new BusinessException("缺失订单编号，请重试!");
        }

        //【2】 从缓存中获取产品信息
        FlashPromotionProduct product = getProductInfo(secKillOrderParam.getFlashPromotionId(),productId);
        //【3】 验证秒杀时间是否超时
        if (!volidateSecKillTime(product)) {
            return CommonResult.failed("秒杀活动未开始或已结束！");
        }


        /*在缓存中扣减库存，应对秒杀高并发*/
        if (!preDecrStockCache(productId)) {
            return CommonResult.failed("已经抢购完了，感谢参与本次活动");
        }
        /*在本地持久化扣减记录*/
        String cfName = OrderConstant.RD_CFNAME_PREFIX + secKillOrderParam.getFlashPromotionId();
        String key = OrderConstant.RD_PRODUCT_PREFIX + productId + OrderConstant.RD_MEMEBER_PREFIX + memberId;
        String value = stockCount+"";
        try {
            RocksDBUtil.put(cfName,key,value);
        } catch (RocksDBException e) {
            log.error("本地持久化异常： ",e);
            return CommonResult.failed("已经抢购完了，感谢参与本次活动");
        }

        //准备创建订单
        //生成下单商品信息
        String orderSn = orderId.toString();
        OmsOrderItem orderItem = new OmsOrderItem();
        orderItem.setId(orderItemId);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductPic(product.getPic());
        orderItem.setProductBrand(product.getBrandName());
        orderItem.setProductSn(product.getProductSn());
        orderItem.setProductPrice(product.getFlashPromotionPrice());
        orderItem.setProductQuantity(1);
        orderItem.setProductCategoryId(product.getProductCategoryId());
        orderItem.setPromotionAmount(product.getPrice().subtract(product.getFlashPromotionPrice()));
        orderItem.setPromotionName("秒杀特惠活动");
        orderItem.setGiftIntegration(product.getGiftPoint());
        orderItem.setGiftGrowth(product.getGiftGrowth());
        orderItem.setCouponAmount(new BigDecimal(0));
        orderItem.setIntegrationAmount(new BigDecimal(0));
        orderItem.setPromotionAmount(new BigDecimal(0));
        //支付金额
        BigDecimal payAmount = product.getFlashPromotionPrice().multiply(new BigDecimal(1));
        //优惠价格
        orderItem.setRealAmount(payAmount);
        orderItem.setOrderSn(orderSn);

        OmsOrder order = new OmsOrder();
        order.setId(orderId);
        order.setDiscountAmount(product.getPrice().subtract(product.getFlashPromotionPrice()));//折扣金额
        order.setFreightAmount(new BigDecimal(0));//运费金额
        order.setPromotionAmount(new BigDecimal(0));
        order.setPromotionInfo("秒杀特惠活动");
        order.setTotalAmount(payAmount);
        order.setIntegration(0);
        order.setIntegrationAmount(new BigDecimal(0));
        order.setPayAmount(payAmount);
        order.setMemberId(memberId);
        order.setMemberUsername(null);
        order.setCreateTime(new Date());
        //设置支付方式：0->未支付,1->支付宝,2->微信
        order.setPayType(secKillOrderParam.getPayType());
        //设置支付方式：0->PC订单,1->APP订单,2->小程序
        order.setSourceType(0);
        //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
        order.setStatus(OrderConstant.ORDER_STATUS_UNPAY);
        //订单类型：0->正常订单；1->秒杀订单
        order.setOrderType(OrderConstant.ORDER_TYPE_SECKILL);
        //用户收货信息
        UmsMemberReceiveAddress address = secKillOrderParam.getMemberReceiveAddress();
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        //0->未确认；1->已确认
        order.setConfirmStatus(OrderConstant.CONFIRM_STATUS_NO);
        order.setDeleteStatus(OrderConstant.DELETE_STATUS_NO);
        //计算赠送积分
        order.setIntegration(product.getGiftPoint());
        //计算赠送成长值
        order.setGrowth(product.getGiftGrowth());
        //生成订单号-理论上唯一
        order.setOrderSn(orderSn);

        /*******************************异步下单******************************************/
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOrder(order);
        orderMessage.setOrderItem(orderItem);
        orderMessage.setFlashPromotionRelationId(product.getRelationId());
        orderMessage.setFlashPromotionLimit(product.getFlashPromotionLimit());
        orderMessage.setFlashPromotionEndDate(product.getFlashPromotionEndDate());
        Map<String,Object> result = new HashMap<>();
        List<OmsOrderItem> itemList = new ArrayList<>();
        itemList.add(orderItem);
        result.put("order",order);
        result.put("orderItemList",itemList);
        try {
            String val = redisStockUtil.get(RedisKeyPrefixConst.MIAOSHA_ASYNC_WAITING_PREFIX + memberId + ":" + productId);
            if (StringUtils.isNotEmpty(val)) {
                if (orderId.toString().equals(val)) {
                    return CommonResult.success(result, "订单创建成功");
                } else if ("1".equals(val)) {
                    return CommonResult.success(result, "排队下单中......，请稍后再试");
                }
            }
            boolean sendStatus = orderMessageSender.sendCreateOrderMsg(orderMessage);
            if(sendStatus){
                /*打上排队的标记*/
                redisStockUtil.set(RedisKeyPrefixConst.MIAOSHA_ASYNC_WAITING_PREFIX + memberId + ":" + productId
                        ,Integer.toString(1),60, TimeUnit.SECONDS);
                result.put("orderStatus",OrderConstant.ORDER_SECKILL_ORDER_TYPE_ASYN);
            }else{
                failSendMessage(productId,result);
                return CommonResult.failed(result,"下单失败，请稍后再试");
            }
        } catch (Exception e) {
            log.error("消息发送失败:error msg:{}",e.getMessage(),e.getCause());
            failSendMessage(productId,result);
            return CommonResult.failed(result,"下单失败，请稍后再试");
        }
        return CommonResult.success(result,"下单中.....，后续请检查下单是否成功");
    }

    /*往MQ发送"创建订单"消息失败时的处理*/
    public void failSendMessage(long productId,Map<String,Object> result){
        /*还原预减库存*/
        incrRedisStock(productId);
        /*清除掉本地guavacache已经售完的标记*/
        cache.remove(RedisKeyPrefixConst.MIAOSHA_STOCK_CACHE_PREFIX + productId);
        /*通知秒杀订单服务群,清除本地售罄标记缓存*/
        if(shouldPublishCleanMsg(productId)){
            redisStockUtil.publish(OrderConstant.REDIS_CLEAN_NO_STOCK_CHANNEL,productId);
        }
        if(null != result){
            result.put("orderStatus",OrderConstant.ORDER_SECKILL_ORDER_TYPE_FAILURE);
        }
    }

    /* 供消息消费者扣减库存和保存订单的api，
    其实变为独立服务或者将消费者直接移到订单微服务更好
    todo 此处应该使用分布式事务，基于seata(不使用mq事务消息了，因为层层嵌套)*/
    @Transactional
    public Long asyncCreateOrder(OmsOrder order,OmsOrderItem orderItem,Long flashPromotionRelationId) {
        //从数据库中扣减库存
        Integer result = promotionFeignApi.descStock(flashPromotionRelationId, 1);
        if (result <= 0) {
            throw new RuntimeException("没抢到！");
        }


        try {
            //插入订单记录
            orderMapper.insertSelective(order);
            //OrderItem关联
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
            //插入orderItem
            orderItemMapper.insertSelective(orderItem);
            /*如果订单创建成功,需要发送定时消息,20min后如果没有支付,则取消当前订单,释放库存*/
            boolean sendStatus = orderMessageSender.sendTimeOutOrderMessage(order.getId() + ":" + flashPromotionRelationId + ":" + orderItem.getProductId());
            if (!sendStatus) {
                throw new RuntimeException("订单超时取消订单消息发送失败！");
            }
        } catch (DuplicateKeyException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("订单超时取消订单消息发送失败！", e);
        }
        return order.getId();
    }

    private static String getSecKillProductIdKey(Long productId) {
        return RedisKeyPrefixConst.MIAOSHA_STOCK_CACHE_PREFIX + productId;
    }

    private boolean preDecrStockCache(Long productId) {
        Long result = redisSingleTemplate.execute(redisScript, Collections.singletonList(getSecKillProductIdKey(productId)),
                1);
        return result != null && result > 0;
    }

    /*Redis中扣减库存*/
    @Deprecated
    private boolean preDecrRedisStock(Long productId) {
        Long stock = redisStockUtil.decr(RedisKeyPrefixConst.MIAOSHA_STOCK_CACHE_PREFIX + productId);
        if (stock < 0) {
            /* 还原缓存里的库存，主要是 当stock < 0时，有订单取消之类回滚库存的操作时，
            会导致增加的库存数量比实际的少，产生这个问题的主要原因是在扣减时未检查库存的数量，
            但是检查库存的数量，又容易导致库存超卖，库存超卖的问题主要是由两个原因引起的，
            一个是查询和扣减不是原子操作，另一个是并发引起的请求无序，
            所以解决这个问题可以采用执行Lua脚本的方法进行库存扣减：
            PO: Lua脚本参考如下，以一行注释一行代码形式呈现：
            -- -------------------Lua脚本代码开始***************************
            -- 调用Redis的get指令，查询活动库存，其中KEYS[1]为传入的参数1，即库存key
            local c_s = redis.call('get', KEYS[1])
            -- 判断活动库存是否充足，其中KEYS[2]为传入的参数2，即当前抢购数量
            if not c_s or tonumber(c_s) < tonumber(KEYS[2]) then
               return 0
            end
            -- 如果活动库存充足，则进行扣减操作。其中KEYS[2]为传入的参数2，即当前抢购数量
            redis.call('decrby',KEYS[1], KEYS[2])
            -- -------------------Lua脚本代码结束***********************
            当然还可以将上面的脚本进行脚本预加载，预加载机制之一可以参考tulingmall-promotion中分布式锁的实现
            */
            incrRedisStock(productId);
            //error 并不需要V4版本中下面这一步，具体原因看 StockSyncReciever.java中的注释
//            /**
//             * notify:<strong>千万注意这里一定不能用setNX,一旦使用,可能出现如果jvm在消息发出去前挂掉了
//             * ,那也就意味着当前产品库存没有办法在卖完后跟DB做同步.</strong>
//             */
//            if(!redisStockUtil.hasKey(RedisKeyPrefixConst.STOCK_REFRESHED_MESSAGE_PREFIX + promotionId)){
//                /*这里这么做的目的非常重要：确保不会发生少卖现象
//                 * 发延时消息,60s后,同步一次库存; 高并发下可能发送多条延时消息，但是没关系，可以容忍*/
//                if(orderMessageSender.sendStockSyncMessage(productId,promotionId)){
//                    redisStockUtil.set(RedisKeyPrefixConst.STOCK_REFRESHED_MESSAGE_PREFIX + promotionId,0);
//                }
//            }
            return false;
        }
        return true;
    }

    //还原库存
    public void incrRedisStock(Long productId){
        if(redisStockUtil.hasKey(RedisKeyPrefixConst.MIAOSHA_STOCK_CACHE_PREFIX + productId)){
            redisStockUtil.incr(RedisKeyPrefixConst.MIAOSHA_STOCK_CACHE_PREFIX + productId);
        }
    }

    /*订单下单前的购买与检查*/
    private CommonResult confirmCheck(Long productId,Long memberId,String token) throws BusinessException {
        /*1、设置标记，如果售罄了在本地cache中设置为true*/
        Boolean localcache = cache.getCache(RedisKeyPrefixConst.MIAOSHA_STOCK_CACHE_PREFIX + productId);
        if(localcache != null && localcache){
            return CommonResult.failed("商品已经售罄,请购买其它商品!");
        }


        //3、从redis缓存当中取出当前要购买的商品库存
        Integer stock = redisStockUtil.get(RedisKeyPrefixConst.MIAOSHA_STOCK_CACHE_PREFIX + productId,Integer.class);

        if(stock == null || stock <= 0){
            /*设置标记，如果售罄了在本地cache中设置为true*/
            cache.setLocalCache(RedisKeyPrefixConst.MIAOSHA_STOCK_CACHE_PREFIX + productId,true);
            return CommonResult.failed("商品已经售罄,请购买其它商品!");
        }

        String async = redisStockUtil.get(RedisKeyPrefixConst.MIAOSHA_ASYNC_WAITING_PREFIX + memberId + ":" + productId);
        if(async != null && async.equals("1")){
            Map<String,Object> result = new HashMap<>();
            result.put("orderStatus","1");//下单方式0->同步下单,1->异步下单排队中,-1->秒杀失败,>1->秒杀成功(返回订单号)
            return CommonResult.failed(result,"异步下单排队中");
        }
        return CommonResult.success(null);
    }

    public boolean shouldPublishCleanMsg(Long productId){
        Integer stock = redisStockUtil.get(RedisKeyPrefixConst.MIAOSHA_STOCK_CACHE_PREFIX + productId,Integer.class);
        return (stock == null || stock <= 0);
    }


    /**
     * 从缓存中获得秒杀的产品信息
     */
    public FlashPromotionProduct getProductInfo(Long flashPromotionId,Long productId){
        String productKey = RedisKeyPrefixConst.SECKILL_PRODUCT_PREFIX + flashPromotionId
                + ":" + productId;
        FlashPromotionProduct result = secKillCache.getIfPresent(productKey);
        if(null == result){
            result = redisStockUtil.get(productKey,FlashPromotionProduct.class);
            if(null == result) return null;
            secKillCache.put(productKey,result);
        }
        return result;
    }

    //验证秒杀时间
    private boolean volidateSecKillTime (FlashPromotionProduct product){
        //当前时间
        Date now = new Date();
        if( product.getFlashPromotionEndDate() == null
                || product.getFlashPromotionStartDate() == null
                || now.after(product.getFlashPromotionEndDate())
                || now.before(product.getFlashPromotionStartDate())){
            return false;
        }
        return true;
    }

    /**
     * 这里使用分布式事务保证cancelSeckillOrder的一致性,注意需要保证服务调用的幂等性
     * @param orderId
     * @param flashPromotionRelationId
     * @param productId
     * @return
     */
    @Override
    @Transactional
    @ShardingTransactionType(TransactionType.BASE)
    public boolean cancelSecKillOrder(Long orderId, Long flashPromotionRelationId,
                                      Long productId) {

        /*
         * prepare阶段， 可以使用AOP来优化，避免代码耦合
         */
        List<OmsOrderCancelMessageHistory> omsOrderCancelMessageHistories = omsOrderCancelMessageHistoryMapper
                .selectByExample(buildQueryCondition(orderId,
                flashPromotionRelationId, productId));
        if (CollectionUtils.isNotEmpty(omsOrderCancelMessageHistories) &&
                omsOrderCancelMessageHistories.get(0).getStatus() == 1) {
            log.warn("已经存在记录，orderId:{}, flashPromotionRelationId:{}, productId:{} ", orderId,
                    flashPromotionRelationId, productId);
            return true;
        }
        OmsOrderCancelMessageHistory consumerHistory;
        boolean newAdd = false;
        if (CollectionUtils.isNotEmpty(omsOrderCancelMessageHistories)) {
            consumerHistory = omsOrderCancelMessageHistories.get(0);
        } else {
            consumerHistory = createConsumerHistory(orderId, flashPromotionRelationId, productId);
            newAdd = true;
        }
        if (newAdd) {
            int insert = omsOrderCancelMessageHistoryMapper.insert(consumerHistory);
            if (insert <= 0) {
                return false;
            }
        }
        OmsOrder omsOrder = orderMapper.selectByPrimaryKey(orderId);

        if(null != omsOrder && omsOrder.getStatus() == OrderConstant.ORDER_STATUS_UNPAY){
            omsOrder.setMemberId(null);
            omsOrder.setStatus(OrderConstant.ORDER_STATUS_CLOSE);
            omsOrder.setGmtModified(new Date());
            orderMapper.updateByPrimaryKeySelective(omsOrder);
            //取消订单,释放DB库存
            promotionFeignApi.incStock(flashPromotionRelationId,1);
            //取消的订单-还原缓存库存
            if(newAdd) {
                /* 只还原一次，避免超卖，
                   其他方案:1. 也可以使用lua脚本CAS操作扣减
                    2. 不直接操作redis,使用mysql累加，充分
                    利用分布式事务,然后人工在后台上维护库存即可。
                */
                incrRedisStock(productId);
            }
        }
        consumerHistory.setStatus(1);
        consumerHistory.setGmtModified(new Date());

        // commit 阶段
        omsOrderCancelMessageHistoryMapper.updateByPrimaryKeySelective(consumerHistory);
        return true;
    }

    private static OmsOrderCancelMessageHistory createConsumerHistory(Long orderId,
                                                                      Long flashPromotionRelationId,
                                                                      Long productId) {
        OmsOrderCancelMessageHistory history = new OmsOrderCancelMessageHistory();
        history.setId(orderId);
        history.setFlashPromotionRelationid(flashPromotionRelationId);
        history.setProductId(productId);
        history.setGmtCreate(new Date());
        return history;
    }

    private static OmsOrderCancelMessageHistoryExample buildQueryCondition(Long orderId,
                                                                           Long flashPromotionRelationId,
                                                                           Long productId) {
        OmsOrderCancelMessageHistoryExample example = new OmsOrderCancelMessageHistoryExample();
        example.createCriteria()
                .andFlashPromotionRelationidEqualTo(flashPromotionRelationId)
                .andOrderIdEqualTo(orderId)
                .andProductIdEqualTo(productId);
        return example;
    }
}
