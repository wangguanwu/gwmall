package com.gw.gwmall.ordercurrent.service.impl;

import com.github.pagehelper.PageHelper;
import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.api.ResultCode;
import com.gw.gwmall.ordercurrent.component.CancelOrderSender;
import com.gw.gwmall.ordercurrent.dao.PortalOrderDao;
import com.gw.gwmall.ordercurrent.dao.PortalOrderItemDao;
import com.gw.gwmall.ordercurrent.domain.*;
import com.gw.gwmall.ordercurrent.feignapi.cart.CartFeignApi;
import com.gw.gwmall.ordercurrent.feignapi.pms.PmsProductStockFeignApi;
import com.gw.gwmall.ordercurrent.feignapi.promotion.PromotionFeignApi;
import com.gw.gwmall.ordercurrent.feignapi.ums.UmsMemberFeignApi;
import com.gw.gwmall.ordercurrent.feignapi.unqid.UnqidFeignApi;
import com.gw.gwmall.ordercurrent.mapper.OmsOrderItemMapper;
import com.gw.gwmall.ordercurrent.mapper.OmsOrderMapper;
import com.gw.gwmall.ordercurrent.mapper.OmsOrderSettingMapper;
import com.gw.gwmall.ordercurrent.model.*;
import com.gw.gwmall.ordercurrent.service.OmsPortalOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 前台订单管理Service
 */
@Service
@Slf4j
public class OmsPortalOrderServiceImpl implements OmsPortalOrderService {

//    @Autowired
//    private OmsCartItemService cartItemService;

    @Autowired
    private UnqidFeignApi unqidFeignApi;

    @Autowired
    private UmsMemberFeignApi umsMemberFeignApi;

    @Autowired
    private PromotionFeignApi promotionFeignApi;

    @Autowired
    private PmsProductStockFeignApi pmsProductStockFeignApi;

//    @Autowired
//    private UmsCouponFeignApi umsCouponFeignApi;


//    @Autowired
//    private UmsIntegrationConsumeSettingMapper integrationConsumeSettingMapper;
//    @Autowired
//    private PmsSkuStockMapper pmsSkuStockMapper;

    @Autowired
    private OmsOrderMapper omsOrderMapper;
    @Autowired
    private PortalOrderItemDao orderItemDao;
//    @Autowired
//    private SmsCouponHistoryMapper couponHistoryMapper;
//    @Autowired
//    private RedisService redisService;
//    @Value("${redis.key.prefix.orderId}")
//    private String REDIS_KEY_PREFIX_ORDER_ID;
    @Autowired
    private PortalOrderDao portalOrderDao;
    @Autowired
    private OmsOrderSettingMapper orderSettingMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    @Autowired
    private CancelOrderSender cancelOrderSender;
    @Autowired
    private CartFeignApi cartFeignApi;

    /**
     * 查询用户订单
     * @param memberId 会员ID
     * @param status  订单状态
     */
    @Override
    public CommonResult<List<OmsOrderDetail>> findMemberOrderList(Integer pageSize, Integer pageNum, Long memberId, Integer status) {
        PageHelper.startPage(pageNum,pageSize);
        return CommonResult.success(portalOrderDao.findMemberOrderList(memberId,status));
    }

    /**
     * 删除订单[逻辑删除],只能status为：3->已完成；4->已关闭；5->无效订单，才可以删除
     * ，否则只能先取消订单然后删除。
     * @param orderId
     * @return  受影响的行数
     */
    @Override
    public int deleteOrder(Long orderId){
        return portalOrderDao.deleteOrder(orderId);
    }

    /**
     * 确认选择购买的商品
     * @param itemIds
     *        选择的购物车商品
     * @return
     */
//    @Override
//    public ConfirmOrderResult generateConfirmOrder(List<Long> itemIds,Long memberId) throws BusinessException {
//        ConfirmOrderResult result = new ConfirmOrderResult();
//        /*获取购物车信息*/
//        //List<CartPromotionItem> cartPromotionItemList = cartFeignApi.listSelectedPromotion(itemIds,memberId);
//        List<CartPromotionItem> cartPromotionItemList = MockService.listSelectedPromotion(itemIds,memberId);
//        result.setCartPromotionItemList(cartPromotionItemList);
//
//        /* 微服务调用获取用户收货地址列表 */
//        //List<UmsMemberReceiveAddress> memberReceiveAddressList = umsMemberFeignApi.list().getData();
//        result.setMemberReceiveAddressList(MockService.getUmsMemberReceiveAddress());
//
//        //TODO 此处可加入其他微服务调用，比如用户积分、用户本人优惠劵等
//        result.setMemberIntegration(0);
//
//        //计算总金额、活动优惠、应付金额
//        ConfirmOrderResult.CalcAmount calcAmount = calcCartAmount(cartPromotionItemList);
//        result.setCalcAmount(calcAmount);
//        return result;
//    }

    /**
     * 生成订单的orderId
     * @param memberId 用户ID
     */
    public Long generateOrderId(Long memberId){
        String leafOrderId = unqidFeignApi.getSegmentId(OrderConstant.LEAF_ORDER_ID_KEY);
        String strMemberId = memberId.toString();
        String OrderIdTail = memberId < 10 ? "0" + strMemberId
                : strMemberId.substring(strMemberId.length() - 2);
        log.debug("生成订单的orderId，组成元素为：{},{}",leafOrderId,OrderIdTail);
        return Long.valueOf(leafOrderId + OrderIdTail);
    }
    /**
     * 生成订单
     * @param orderParam
     * @return
     */
    @Override
    //@GlobalTransactional(name = "generateOrder",rollbackFor = Exception.class)
    @Transactional
    public CommonResult generateOrder(OrderParam orderParam, Long memberId) {
        log.debug("接受参数OrderParam：{} memberId：{}",orderParam,memberId);
        if(null == orderParam || null == memberId){
            return CommonResult.failed(ResultCode.VALIDATE_FAILED,"参数不能为空！");
        }
        Long orderId = orderParam.getOrderId();
        if(null == orderId){
            orderId = generateOrderId(memberId);
            log.debug("前端页面未传递orderId，临时生成：{}",orderId);
        }else{
            log.debug("前端页面传递orderId[{}]",orderId);
        }
        /*这里我们对OrderSn简单处理，在实际业务时可以根据情况做变化，比如添加前缀或可逆加密，
        只要保证可以从OrderSn中解析出orderId即可*/
        String orderSn = orderId.toString();

        List<OmsOrderItem> orderItemList = new ArrayList<>();

        List<CartPromotionItem> cartPromotionItemList = cartFeignApi.listSelectedPromotion(orderParam.getItemIds());
        int itemSize = cartPromotionItemList.size();

        /*一次获取多个OrderItem的id，但是可能获取的数量少于订单详情数*/
        List<String> omsOrderItemIDList = unqidFeignApi.getSegmentIdList(OrderConstant.LEAF_ORDER_ITEM_ID_KEY,
                itemSize);
        log.debug("获得订单详情的ID：{}，需求{}个，实际{}个" ,omsOrderItemIDList,itemSize,omsOrderItemIDList.size());
        int itemListIndex = 0;
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            //生成下单商品信息
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setProductId(cartPromotionItem.getProductId());
            orderItem.setProductName(cartPromotionItem.getProductName());
            orderItem.setProductPic(cartPromotionItem.getProductPic());
            orderItem.setProductAttr(cartPromotionItem.getProductAttr());
            orderItem.setProductBrand(cartPromotionItem.getProductBrand());
            orderItem.setProductSn(cartPromotionItem.getProductSn());
            orderItem.setProductPrice(cartPromotionItem.getPrice());
            orderItem.setProductQuantity(cartPromotionItem.getQuantity());
            orderItem.setProductSkuId(cartPromotionItem.getProductSkuId());
            orderItem.setProductSkuCode(cartPromotionItem.getProductSkuCode());
            orderItem.setProductCategoryId(cartPromotionItem.getProductCategoryId());
            orderItem.setPromotionAmount(cartPromotionItem.getReduceAmount());
            orderItem.setPromotionName(cartPromotionItem.getPromotionMessage());
            orderItem.setGiftIntegration(cartPromotionItem.getIntegration());
            orderItem.setGiftGrowth(cartPromotionItem.getGrowth());
            orderItem.setOrderId(orderId);
            orderItem.setOrderSn(orderSn);
            if(itemListIndex < itemSize){
                orderItem.setId(Long.valueOf(omsOrderItemIDList.get(itemListIndex)));
            }else{
                log.warn("从分布式ID服务获得的id已经用完，可能订单详情太多或分布式ID服务出错，请检查！" +
                        "正尝试每个订单详情单独获得id");
                orderItem.setId(Long.valueOf(unqidFeignApi.getSegmentId(OrderConstant.LEAF_ORDER_ITEM_ID_KEY)));
            }
            //判断是否使用了优惠券
            if (orderParam.getCouponId() == null) {
                //不用优惠券
                for (OmsOrderItem orderItemTemp : orderItemList) {
                    orderItemTemp.setCouponAmount(new BigDecimal(0));
                }
            } else {
                //使用优惠券
                SmsCouponHistoryDetail couponHistoryDetail = getUseCoupon(cartPromotionItemList, orderParam.getCouponId());
                if (couponHistoryDetail == null) {
                    return CommonResult.failed("该优惠券不可用");
                }
                //对下单商品的优惠券进行处理
                handleCouponAmount(orderItemList, couponHistoryDetail);
            }
            orderItem.setCouponAmount(new BigDecimal(0));
            orderItem.setIntegrationAmount(new BigDecimal(0));
            orderItemList.add(orderItem);
            itemListIndex++;
        }

        //计算order_item的实付金额
        handleRealAmount(orderItemList);
        //todo 分布式事务 进行库存锁定
        CommonResult lockResult = pmsProductStockFeignApi.lockStock(cartPromotionItemList);
        if(lockResult.getCode() ==ResultCode.FAILED.getCode()) {
            log.warn("远程调用锁定库存失败");
            throw new RuntimeException("远程调用锁定库存失败");
        }
        OmsOrder order = new OmsOrder();
        order.setId(orderId);
        order.setDiscountAmount(new BigDecimal(0));
        order.setTotalAmount(calcTotalAmount(orderItemList));
        order.setFreightAmount(new BigDecimal(0));
        order.setPromotionAmount(new BigDecimal(0));
        order.setPromotionInfo("无优惠");
        order.setCouponAmount(new BigDecimal(0));
        order.setIntegration(0);
        order.setIntegrationAmount(new BigDecimal(0));

        order.setPayAmount(calcPayAmount(order));
        //转化为订单信息并插入数据库
        order.setMemberId(memberId);
        order.setCreateTime(new Date());
        order.setMemberUsername(null);
        //支付方式：0->未支付；1->支付宝；2->微信
        order.setPayType(orderParam.getPayType());
        order.setSourceType(OrderConstant.SOURCE_TYPE_APP);
        order.setStatus(OrderConstant.ORDER_STATUS_UNPAY);
        order.setOrderType(OrderConstant.ORDER_TYPE_NORMAL);
        //收货人信息：姓名、电话、邮编、地址
        /* TODO 通过Feign远程调用 会员服务*/
        UmsMemberReceiveAddress address = umsMemberFeignApi.getItem(orderParam.getMemberReceiveAddressId()).getData();
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        order.setConfirmStatus(OrderConstant.CONFIRM_STATUS_NO);
        order.setDeleteStatus(OrderConstant.DELETE_STATUS_NO);
        //计算赠送积分
        order.setIntegration(0);
        //计算赠送成长值
        order.setGrowth(0);
        order.setOrderSn(orderSn);
        //插入order表和order_item表
        omsOrderMapper.insert(order);
        orderItemDao.insertList(orderItemList);

        //TODO 分布式事务 删除购物车中的下单商品
//        deleteCartItemList(cartPromotionItemList, memberId);
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItemList);
        return CommonResult.success(result, "下单成功");
    }

    /**
     * 订单详情
     * @param orderId
     */
    public CommonResult getDetailOrder(Long orderId){
        return CommonResult.success(portalOrderDao.getDetail(orderId));
    }

    @Override
    public Integer paySuccess(Long orderId,Integer payType) {
        OmsOrderDetail orderDetail = portalOrderDao.getDetail(orderId);
        //订单已经超时关闭了，这时再支付就没用了。
        if(orderDetail.getStatus().equals(5)){
            log.warn("订单"+orderDetail.getOrderSn()+"已经关闭，无法正常支付。请发起支付宝订单退款接口。");
            //TODO 发起支付宝订单退款
            return -1;
        }
        //修改订单支付状态
        OmsOrder order = new OmsOrder();
        order.setId(orderId);
        order.setStatus(OrderConstant.ORDER_STATUS_UNDELIVERY);
        order.setPayType(payType);
        order.setPaymentTime(new Date());
        omsOrderMapper.updateByPrimaryKeySelective(order);

        List<StockChanges> stockChangesList = new ArrayList<>();
        for(OmsOrderItem omsOrderItem : orderDetail.getOrderItemList()){
            stockChangesList.add(new StockChanges(omsOrderItem.getProductSkuId(),omsOrderItem.getProductQuantity()));
        }
        /*实际进行真实库存的扣减*/
        // todo 分布式事务
        // PO :可以使用MQ进行异步扣减
        CommonResult lockResult = pmsProductStockFeignApi.reduceStock(stockChangesList);
        if(lockResult.getCode() ==ResultCode.FAILED.getCode()) {
            log.warn("远程调用真实库存的扣减失败");
            return -1;
            //throw new RuntimeException("远程调用真实库存的扣减失败");
        }else{
            log.debug("远程调用真实库存的扣减成功");
            return (Integer) lockResult.getData();
        }
    }

    @Override
    public CommonResult cancelTimeOutOrder() {
        OmsOrderSetting orderSetting = orderSettingMapper.selectByPrimaryKey(1L);
        //查询超时、未支付的订单及订单详情
        List<OmsOrderDetail> timeOutOrders = portalOrderDao.getTimeOutOrders(orderSetting.getNormalOrderOvertime());
        if (CollectionUtils.isEmpty(timeOutOrders)) {
            return CommonResult.failed("暂无超时订单");
        }
        //修改订单状态为交易取消
        List<Long> ids = new ArrayList<>();
        List<StockChanges> stockChangesList = new ArrayList<>();
        for (OmsOrderDetail timeOutOrder : timeOutOrders) {
            ids.add(timeOutOrder.getId());
            if(CollectionUtils.isEmpty(timeOutOrder.getOrderItemList())){
                log.warn("订单{}没有下没有商品详情，请检查该订单！",timeOutOrder.getId());
            }
            //解除订单商品库存锁定
            for(OmsOrderItem omsOrderItem : timeOutOrder.getOrderItemList()){
                stockChangesList.add(new StockChanges(omsOrderItem.getProductSkuId(),omsOrderItem.getProductQuantity()));
            }
        }
        portalOrderDao.updateOrderStatus(ids, OrderConstant.ORDER_STATUS_CLOSE);
        if (!CollectionUtils.isEmpty(stockChangesList)) {
            pmsProductStockFeignApi.recoverStock(stockChangesList);
        }

//        for (OmsOrderDetail timeOutOrder : timeOutOrders) {
//            //修改优惠券使用状态
//            //updateCouponStatus(timeOutOrder.getCouponId(), timeOutOrder.getMemberId(), 0);
//        }
        return CommonResult.success(null);
    }

    @Override
    public void cancelOrder(Long orderId,Long memberId) {
        //查询为付款的取消订单
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andIdEqualTo(orderId).andStatusEqualTo(0).andDeleteStatusEqualTo(0);
        List<OmsOrder> cancelOrderList = omsOrderMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(cancelOrderList)) {
            return;
        }
        OmsOrder cancelOrder = cancelOrderList.get(0);
        if (cancelOrder != null) {
            //修改订单状态为取消
            cancelOrder.setStatus(OrderConstant.ORDER_STATUS_CLOSE);
            /*MemberId为分片键，不能修改*/
            cancelOrder.setMemberId(null);
            omsOrderMapper.updateByPrimaryKeySelective(cancelOrder);
            OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
            orderItemExample.createCriteria().andOrderIdEqualTo(orderId);
            List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
            List<StockChanges> stockChangesList = new ArrayList<>();
            for(OmsOrderItem omsOrderItem : orderItemList){
                stockChangesList.add(new StockChanges(omsOrderItem.getProductSkuId(),omsOrderItem.getProductQuantity()));
            }
            //解除订单商品库存锁定
            if (!CollectionUtils.isEmpty(stockChangesList)) {
                pmsProductStockFeignApi.recoverStock(stockChangesList);
            }
            //修改优惠券使用状态
            //updateCouponStatus(cancelOrder.getCouponId(), cancelOrder.getMemberId(), 0);
        }
    }

    @Override
    public void sendDelayMessageCancelOrder(MqCancelOrder mqCancelOrder) {
        //获取订单超时时间
//        OmsOrderSetting orderSetting = orderSettingMapper.selectByPrimaryKey(1L);
//        long delayTimes = orderSetting.getNormalOrderOvertime() * 60 * 1000;
        long delayTimes = 5000L;
        //发送延迟消息
        cancelOrderSender.sendMessage(mqCancelOrder, delayTimes);
    }

    /**
     * 删除下单商品的购物车信息
     * todo 分布式事务
     */
    private void deleteCartItemList(List<CartPromotionItem> cartPromotionItemList,Long memberId) {
        List<Long> ids = new ArrayList<>();
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            ids.add(cartPromotionItem.getId());
        }
        //TODO cartItemService.delete(memberId, ids);
    }

//    /**
//     * 计算该订单赠送的成长值
//     */
//    private Integer calcGiftGrowth(List<OmsOrderItem> orderItemList) {
//        Integer sum = 0;
//        for (OmsOrderItem orderItem : orderItemList) {
//            sum = sum + orderItem.getGiftGrowth() * orderItem.getProductQuantity();
//        }
//        return sum;
//    }

//    /**
//     * 计算该订单赠送的积分
//     */
//    private Integer calcGifIntegration(List<OmsOrderItem> orderItemList) {
//        int sum = 0;
//        for (OmsOrderItem orderItem : orderItemList) {
//            sum += orderItem.getGiftIntegration() * orderItem.getProductQuantity();
//        }
//        return sum;
//    }

//    /**
//     * 将优惠券信息更改为指定状态
//     *
//     * @param couponId  优惠券id
//     * @param memberId  会员id
//     * @param useStatus 0->未使用；1->已使用
//     */
//    private void updateCouponStatus(Long couponId, Long memberId, Integer useStatus) {
//        if (couponId == null) return;
//        //查询第一张优惠券
//        SmsCouponHistoryExample example = new SmsCouponHistoryExample();
//        example.createCriteria().andMemberIdEqualTo(memberId)
//                .andCouponIdEqualTo(couponId).andUseStatusEqualTo(useStatus == 0 ? 1 : 0);
//        List<SmsCouponHistory> couponHistoryList = couponHistoryMapper.selectByExample(example);
//        if (!CollectionUtils.isEmpty(couponHistoryList)) {
//            SmsCouponHistory couponHistory = couponHistoryList.get(0);
//            couponHistory.setUseTime(new Date());
//            couponHistory.setUseStatus(useStatus);
//            couponHistoryMapper.updateByPrimaryKeySelective(couponHistory);
//        }
//    }

    private void handleRealAmount(List<OmsOrderItem> orderItemList) {
        for (OmsOrderItem orderItem : orderItemList) {
            //原价-促销优惠-优惠券抵扣-积分抵扣
            BigDecimal realAmount = orderItem.getProductPrice();
            if (null!=orderItem.getPromotionAmount()){
                realAmount.subtract(orderItem.getPromotionAmount());
            }
            if (null!=orderItem.getCouponAmount()){
                realAmount.subtract(orderItem.getCouponAmount());
            }
            if (null!=orderItem.getIntegrationAmount()){
                realAmount.subtract(orderItem.getIntegrationAmount());
            }
           orderItem.setRealAmount(realAmount);
        }
    }

//    /**
//     * 获取订单促销信息
//     */
//    private String getOrderPromotionInfo(List<OmsOrderItem> orderItemList) {
//        StringBuilder sb = new StringBuilder();
//        for (OmsOrderItem orderItem : orderItemList) {
//            sb.append(orderItem.getPromotionName());
//            sb.append(",");
//        }
//        String result = sb.toString();
//        if (result.endsWith(",")) {
//            result = result.substring(0, result.length() - 1);
//        }
//        return result;
//    }

    /**
     * 计算订单应付金额
     */
    private BigDecimal calcPayAmount(OmsOrder order) {
        //总金额+运费-促销优惠-优惠券优惠-积分抵扣
        BigDecimal payAmount = order.getTotalAmount()
                .add(order.getFreightAmount())
                .subtract(order.getPromotionAmount())
                .subtract(order.getCouponAmount())
                .subtract(order.getIntegrationAmount());
        return payAmount;
    }

//    /**
//     * 计算积分优惠券金额
//     */
//    private BigDecimal calcIntegrationAmount(List<OmsOrderItem> orderItemList) {
//        BigDecimal integrationAmount = new BigDecimal(0);
//        for (OmsOrderItem orderItem : orderItemList) {
//            if (orderItem.getIntegrationAmount() != null) {
//                integrationAmount = integrationAmount.add(orderItem.getIntegrationAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
//            }
//        }
//        return integrationAmount;
//    }

//    /**
//     * 计算订单优惠券金额
//     */
//    private BigDecimal calcCouponAmount(List<OmsOrderItem> orderItemList) {
//        BigDecimal couponAmount = new BigDecimal(0);
//        for (OmsOrderItem orderItem : orderItemList) {
//            if (orderItem.getCouponAmount() != null) {
//                couponAmount = couponAmount.add(orderItem.getCouponAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
//            }
//        }
//        return couponAmount;
//    }

//    /**
//     * 计算订单促销优惠
//     */
//    private BigDecimal calcPromotionAmount(List<OmsOrderItem> orderItemList) {
//        BigDecimal promotionAmount = new BigDecimal(0);
//        for (OmsOrderItem orderItem : orderItemList) {
//            if (orderItem.getPromotionAmount() != null) {
//                promotionAmount = promotionAmount.add(orderItem.getPromotionAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
//            }
//        }
//        return promotionAmount;
//    }

//    /**
//     * 获取可用积分抵扣金额
//     *
//     * @param useIntegration 使用的积分数量
//     * @param totalAmount    订单总金额
//     * @param memberIntegration  用户的积分
//     * @param hasCoupon      是否已经使用优惠券
//     */
//    private BigDecimal getUseIntegrationAmount(Integer useIntegration, BigDecimal totalAmount, Integer memberIntegration, boolean hasCoupon) {
//        BigDecimal zeroAmount = new BigDecimal(0);
//        //判断用户是否有这么多积分
//        if (useIntegration.compareTo(memberIntegration) > 0) {
//            return zeroAmount;
//        }
//        //根据积分使用规则判断是否可用
//        //是否可与优惠券共用
//        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectByPrimaryKey(1L);
//        if (hasCoupon && integrationConsumeSetting.getCouponStatus().equals(0)) {
//            //不可与优惠券共用
//            return zeroAmount;
//        }
//        //是否达到最低使用积分门槛
//        if (useIntegration.compareTo(integrationConsumeSetting.getUseUnit()) < 0) {
//            return zeroAmount;
//        }
//        //是否超过订单抵用最高百分比
//        BigDecimal integrationAmount = new BigDecimal(useIntegration).divide(new BigDecimal(integrationConsumeSetting.getUseUnit()), 2, RoundingMode.HALF_EVEN);
//        BigDecimal maxPercent = new BigDecimal(integrationConsumeSetting.getMaxPercentPerOrder()).divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);
//        if (integrationAmount.compareTo(totalAmount.multiply(maxPercent)) > 0) {
//            return zeroAmount;
//        }
//        return integrationAmount;
//    }

    /**
     * 对优惠券优惠进行处理
     *
     * @param orderItemList       order_item列表
     * @param couponHistoryDetail 可用优惠券详情
     */
    private void handleCouponAmount(List<OmsOrderItem> orderItemList, SmsCouponHistoryDetail couponHistoryDetail) {
        SmsCoupon coupon = couponHistoryDetail.getCoupon();
        if (coupon.getUseType().equals(0)) {
            //全场通用
            calcPerCouponAmount(orderItemList, coupon);
        } else if (coupon.getUseType().equals(1)) {
            //指定分类
            List<OmsOrderItem> couponOrderItemList = getCouponOrderItemByRelation(couponHistoryDetail, orderItemList, 0);
            calcPerCouponAmount(couponOrderItemList, coupon);
        } else if (coupon.getUseType().equals(2)) {
            //指定商品
            List<OmsOrderItem> couponOrderItemList = getCouponOrderItemByRelation(couponHistoryDetail, orderItemList, 1);
            calcPerCouponAmount(couponOrderItemList, coupon);
        }
    }

    /**
     * 对每个下单商品进行优惠券金额分摊的计算
     *
     * @param orderItemList 可用优惠券的下单商品商品
     */
    private void calcPerCouponAmount(List<OmsOrderItem> orderItemList, SmsCoupon coupon) {
        BigDecimal totalAmount = calcTotalAmount(orderItemList);
        for (OmsOrderItem orderItem : orderItemList) {
            //(商品价格/可用商品总价)*优惠券面额
            BigDecimal couponAmount = orderItem.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN).multiply(coupon.getAmount());
            orderItem.setCouponAmount(couponAmount);
        }
    }

    /**
     * 获取与优惠券有关系的下单商品
     *
     * @param couponHistoryDetail 优惠券详情
     * @param orderItemList       下单商品
     * @param type                使用关系类型：0->相关分类；1->指定商品
     */
    private List<OmsOrderItem> getCouponOrderItemByRelation(SmsCouponHistoryDetail couponHistoryDetail, List<OmsOrderItem> orderItemList, int type) {
        List<OmsOrderItem> result = new ArrayList<>();
        if (type == 0) {
            List<Long> categoryIdList = new ArrayList<>();
            for (SmsCouponProductCategoryRelation productCategoryRelation : couponHistoryDetail.getCategoryRelationList()) {
                categoryIdList.add(productCategoryRelation.getProductCategoryId());
            }
            for (OmsOrderItem orderItem : orderItemList) {
                if (categoryIdList.contains(orderItem.getProductCategoryId())) {
                    result.add(orderItem);
                } else {
                    orderItem.setCouponAmount(new BigDecimal(0));
                }
            }
        } else if (type == 1) {
            List<Long> productIdList = new ArrayList<>();
            for (SmsCouponProductRelation productRelation : couponHistoryDetail.getProductRelationList()) {
                productIdList.add(productRelation.getProductId());
            }
            for (OmsOrderItem orderItem : orderItemList) {
                if (productIdList.contains(orderItem.getProductId())) {
                    result.add(orderItem);
                } else {
                    orderItem.setCouponAmount(new BigDecimal(0));
                }
            }
        }
        return result;
    }

    /**
     * 获取该用户可以使用的优惠券
     *
     * @param cartPromotionItemList 购物车优惠列表
     * @param couponId              使用优惠券id
     */
    private SmsCouponHistoryDetail getUseCoupon(List<CartPromotionItem> cartPromotionItemList, Long couponId) {
        //远程调用可用优惠卷列表
        CommonResult<List<SmsCouponHistoryDetail>> couponResult = promotionFeignApi.listCartCoupons(1,cartPromotionItemList);
        if(ResultCode.SUCCESS.getCode() == couponResult.getCode()){
            List<SmsCouponHistoryDetail> couponHistoryDetailList = couponResult.getData();
            for (SmsCouponHistoryDetail couponHistoryDetail : couponHistoryDetailList) {
                if (couponHistoryDetail.getCoupon().getId().equals(couponId)) {
                    return couponHistoryDetail;
                }
            }
        }
        return null;
    }

    /**
     * 计算总金额
     */
    private BigDecimal calcTotalAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsOrderItem item : orderItemList) {
            totalAmount = totalAmount.add(item.getProductPrice().multiply(new BigDecimal(item.getProductQuantity())));
        }
        return totalAmount;
    }

    /**
     * 计算购物车中商品的价格
     */
    private ConfirmOrderResult.CalcAmount calcCartAmount(List<CartPromotionItem> cartPromotionItemList) {
        ConfirmOrderResult.CalcAmount calcAmount = new ConfirmOrderResult.CalcAmount();
        calcAmount.setFreightAmount(new BigDecimal(0));
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal promotionAmount = new BigDecimal("0");
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            totalAmount = totalAmount.add(cartPromotionItem.getPrice().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
            if (null!=cartPromotionItem.getReduceAmount()) {
                promotionAmount = promotionAmount.add(cartPromotionItem.getReduceAmount().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
            }
        }
        calcAmount.setTotalAmount(totalAmount);
        calcAmount.setPromotionAmount(promotionAmount);
        calcAmount.setPayAmount(totalAmount.subtract(promotionAmount));
        return calcAmount;
    }

}
