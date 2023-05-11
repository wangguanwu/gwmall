package com.gw.gwmall.cart.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.gw.gwmall.cart.component.LocalCache;
import com.gw.gwmall.cart.domain.CartPromotionItem;
import com.gw.gwmall.cart.domain.ConfirmOrderResult;
import com.gw.gwmall.cart.feignapi.unqid.UnqidFeignApi;
import com.gw.gwmall.cart.model.UmsMember;
import com.gw.gwmall.cart.model.UmsMemberReceiveAddress;
import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.constant.RedisKeyPrefixConst;
import com.gw.gwmall.common.constant.RedisMemberPrefix;
import com.gw.gwmall.common.exception.BusinessException;

import com.gw.gwmall.domain.FlashPromotionProduct;
import com.gw.gwmall.domain.PortalMemberInfo;
import com.gw.gwmall.rediscomm.util.RedisOpsExtUtil;
import com.gw.gwmall.rediscomm.util.RedisSingleUtil;
import com.gw.gwmall.cart.service.SecKillConfirmOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.gw.gwmall.cart.service.impl.RefreshIdListTask.LEAF_ORDER_ID_KEY;
import static com.gw.gwmall.cart.service.impl.RefreshIdListTask.LEAF_ORDER_ITEM_ID_KEY;

/**
 * @description:
 **/
@Slf4j
@Service
public class SecKillConfirmOrderServiceImpl implements SecKillConfirmOrderService {

    @Resource
    private RedisOpsExtUtil redisOpsUtil;

    @Resource
    private RedisSingleUtil redisStockUtil;

    @Resource
    private LocalCache<Boolean> cache;

    @Resource
    private UnqidFeignApi unqidFeignApi;

    @Resource
    private Cache<String, FlashPromotionProduct> secKillCache;

    /*存放预制orderId的list，同时也可限流每秒允许个数，在更高并发的请求下，
    可以使用Disruptor代替 https://github.com/LMAX-Exchange/disruptor/wiki */
    private final AbstractQueue<String> orderIdQueue = new ConcurrentLinkedQueue<>();
    private final AbstractQueue<String> orderItemIdQueue = new ConcurrentLinkedQueue<>();
    public static final int ORDER_COUNT_LIMIT_SECOND = 2000;
    public static final int FETCH_PERIOD = 100;
    private final ScheduledExecutorService refreshService = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init(){
        List<String> segmentIdList = unqidFeignApi.getSegmentIdList(LEAF_ORDER_ID_KEY, ORDER_COUNT_LIMIT_SECOND);
        orderIdQueue.addAll(segmentIdList);
        List<String> segmentItemIdList = unqidFeignApi.getSegmentIdList(LEAF_ORDER_ITEM_ID_KEY, ORDER_COUNT_LIMIT_SECOND);
        orderItemIdQueue.addAll(segmentItemIdList);
        refreshService.scheduleAtFixedRate(new RefreshIdListTask(orderIdQueue,unqidFeignApi, orderItemIdQueue),
                0, FETCH_PERIOD, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void close(){
        refreshService.shutdown();
    }

    private static String convertToStr(Long memberId) {
        if(memberId < 10) {
            return "0" + memberId;
        }
        String memberStr = memberId.toString();
        return memberStr.substring(memberStr.length() - 2);
    }

    /*秒杀订单确认信息*/
    @Override
    public CommonResult generateConfirmSecKillOrder(Long productId
            , Long memberId, String token, Long flashPromotionId) throws BusinessException {

        String orderIdStr = orderIdQueue.poll();
        String orderItemIdStr = orderItemIdQueue.poll();
        if (null == orderIdStr || null ==orderItemIdStr) {
            //限流措施
            return CommonResult.failed("活动太火爆了，稍后再试试吧...");
        }

        //基因法分表会用到
        orderIdStr = orderItemIdStr + convertToStr(memberId);

            //【1】 进行订单金额确认前的库存与购买权限检查
        CommonResult commonResult = confirmCheck(productId,memberId,token);
        if(commonResult.getCode() == 500){
            return commonResult;
        }
        // 【2】从缓存中获得用户信息
        PortalMemberInfo member = redisOpsUtil.get(RedisMemberPrefix.MEMBER_INFO_PREFIX + memberId, UmsMember.class);
        if(null == member){
            log.error("用户[{}}]还未登录，请检查",memberId);
            return CommonResult.failed("你还未登录，请重新登录！");
        }

        // 【3】从缓存中获得秒杀的产品信息
        FlashPromotionProduct product = getProductInfo(flashPromotionId,productId);

        if(product == null){
            return CommonResult.failed("无效的商品！");
        }

        //【4】 验证秒杀时间是否超时
        if(!validateSecKillTime(product)){
            return CommonResult.failed("不在秒杀活动时间范围！");
        }

        ConfirmOrderResult result = new ConfirmOrderResult();
        result.setOrderId(Long.valueOf(orderIdStr));

        //【5】 获取用户收货地址列表
        List<UmsMemberReceiveAddress> memberReceiveAddressList =
                redisOpsUtil.getListAll(RedisMemberPrefix.MEMBER_ADDRESS_PREFIX+memberId,
                        UmsMemberReceiveAddress.class);
        result.setMemberReceiveAddressList(memberReceiveAddressList);

        /*【6】构建商品信息，本项目设计中，秒杀活动中一次下单只允许一种商品而且数量一个，
        不过在数据结构的设计上，是允许多个商品，每个商品多个数量,
        想要支持多商品多数量，需要修改从前端页面到后端接口很多地方*/
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();
        CartPromotionItem promotionItem = new CartPromotionItem();
        promotionItem.setOrderItemId(Long.valueOf(orderItemIdStr));
        promotionItem.setProductSubTitle(product.getSubTitle());
        promotionItem.setPrice(product.getPrice());
        promotionItem.setProductId(product.getId());//产品ID
        promotionItem.setProductName(product.getName());//产品名称
        promotionItem.setMemberId(memberId);//会员ID
        promotionItem.setMemberNickname(member.getNickname());//昵称
        promotionItem.setProductPic(product.getPic());//产品主图
        promotionItem.setProductBrand(product.getBrandName());//品牌
        promotionItem.setQuantity(1);//购买数量,一次只能秒杀一件
        Integer stock = redisStockUtil.get(String.format(RedisKeyPrefixConst.MIAOSHA_STOCK_CACHE_PREFIX +"{}",
                productId.toString()),Integer.class);
        promotionItem.setRealStock(stock);//库存
        promotionItem.setProductCategoryId(product.getProductCategoryId());//产品类目ID
        promotionItem.setReduceAmount(product.getPrice().subtract(product.getFlashPromotionPrice()));//计算秒杀优惠价格
        promotionItem.setPromotionMessage("秒杀特惠活动");
        cartPromotionItemList.add(promotionItem);
        result.setCartPromotionItemList(cartPromotionItemList);
        //【7】 计算订单总金额
        ConfirmOrderResult.CalcAmount calcAmount = calcCartAmount(product);
        result.setCalcAmount(calcAmount);
        return CommonResult.success(result);
    }

    /*从缓存中获得秒杀的产品信息*/
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

    /**
     * 计算总金额
     */
    private ConfirmOrderResult.CalcAmount calcCartAmount(FlashPromotionProduct product) {
        ConfirmOrderResult.CalcAmount calcAmount = new ConfirmOrderResult.CalcAmount();
        calcAmount.setFreightAmount(new BigDecimal(0));
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal promotionAmount = new BigDecimal("0");
        totalAmount = totalAmount.add(product.getFlashPromotionPrice()
                .multiply(new BigDecimal(1)));
        calcAmount.setTotalAmount(totalAmount);
        calcAmount.setPromotionAmount(promotionAmount);
        calcAmount.setPayAmount(totalAmount);
        return calcAmount;
    }

    //验证秒杀时间
    private boolean validateSecKillTime(FlashPromotionProduct product){
        //当前时间
        Date now = new Date();
        if(product.getFlashPromotionEndDate() == null
                || product.getFlashPromotionStartDate() == null
                || now.after(product.getFlashPromotionEndDate())
                || now.before(product.getFlashPromotionStartDate())){
            return false;
        }
        return true;
    }

    /*订单下单前的购买与检查*/
    private CommonResult confirmCheck(Long productId,Long memberId,String token) {
        /*1、设置标记，如果售罄了在本地cache中设置为true*/
        Boolean localcache = cache.getCache(RedisKeyPrefixConst.MIAOSHA_STOCK_CACHE_PREFIX + productId);
        if(localcache != null && localcache){
            return CommonResult.failed("商品已经售罄,请购买其它商品!");
        }

        //2、从redis缓存当中取出当前要购买的商品库存
        Integer stock = redisStockUtil.get(RedisKeyPrefixConst.MIAOSHA_STOCK_CACHE_PREFIX + productId,Integer.class);

        if (stock == null || stock <= 0) {
            /*设置标记，如果售罄了在本地cache中设置为true*/
            cache.setLocalCache(RedisKeyPrefixConst.MIAOSHA_STOCK_CACHE_PREFIX + productId,true);
            return CommonResult.failed("商品已经售罄,请购买其它商品!");
        }

        /*"1" 的含义参见 OrderConstant ORDER_SECKILL_ORDER_TYPE_ASYN*/
        String async = redisStockUtil.get(RedisKeyPrefixConst.MIAOSHA_ASYNC_WAITING_PREFIX + memberId + ":" + productId);
        if (async != null && async.equals("1")) {
            Map<String,Object> result = new HashMap<>();
            result.put("orderStatus","1");//下单方式0->同步下单,1->异步下单排队中,-1->秒杀失败,>1->秒杀成功(返回订单号)
            return CommonResult.failed(result,"异步下单排队中");
        }
        return CommonResult.success(null);
    }

}
