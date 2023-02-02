package com.gw.gwmall.promotion.service.impl;


import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.promotion.dao.SmsCouponHistoryDao;
import com.gw.gwmall.promotion.domain.CartPromotionItem;
import com.gw.gwmall.promotion.domain.SmsCouponHistoryDetail;
import com.gw.gwmall.promotion.mapper.SmsCouponHistoryMapper;
import com.gw.gwmall.promotion.mapper.SmsCouponMapper;
import com.gw.gwmall.promotion.model.*;
import com.gw.gwmall.promotion.service.UserCouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 会员优惠券管理Service实现类
 */
@Slf4j
@Service
public class UserCouponServiceImpl implements UserCouponService {

    @Autowired
    private SmsCouponMapper smsCouponMapper;
    @Autowired
    private SmsCouponHistoryMapper smsCouponHistoryMapper;
    @Autowired
    private SmsCouponHistoryDao smsCouponHistoryDao;
    @Transactional
    @Override
    public CommonResult activelyGet(Long couponId, Long memberId, String nickName) {

        //获取优惠券信息，判断数量
        SmsCoupon coupon = smsCouponMapper.selectByPrimaryKey(couponId);
        if(coupon==null){
            return CommonResult.failed("优惠券不存在");
        }
        if(coupon.getCount()<=0){
            return CommonResult.failed("优惠券已经领完了");
        }
        Date now = new Date();
        if(now.before(coupon.getEnableTime())){
            return CommonResult.failed("优惠券还没到领取时间");
        }
        //判断用户领取的优惠券数量是否超过限制
        SmsCouponHistoryExample couponHistoryExample = new SmsCouponHistoryExample();
        couponHistoryExample.createCriteria().andCouponIdEqualTo(couponId).andMemberIdEqualTo(memberId);
        long count = smsCouponHistoryMapper.countByExample(couponHistoryExample);
        if(count>=coupon.getPerLimit()){
            return CommonResult.failed("已经领取过该优惠券");
        }
        //生成领取优惠券历史
        SmsCouponHistory couponHistory = new SmsCouponHistory();
        couponHistory.setCouponId(couponId);
        couponHistory.setCouponCode(generateCouponCode(memberId));
        couponHistory.setCreateTime(now);
        couponHistory.setMemberId(memberId);
        couponHistory.setMemberNickname(nickName);
        //主动领取
        couponHistory.setGetType(ConstantPromotion.USER_COUPON_GET_TYPE_PROACTIVE);
        //未使用
        couponHistory.setUseStatus(ConstantPromotion.USER_COUPON_USE_STATE_UNUSE);
        smsCouponHistoryMapper.insert(couponHistory);
        /*修改优惠券表的数量、领取数量
        这种实现无疑有并发问题，比如AB用户同时领取优惠券，此时优惠券数量为100，那么在写库的时候
        A用户set数量100 - 1，B用户set数量减100 - 1，而其实应该 100 - 2
        领取数量receive_count同理
        coupon.setCount(coupon.getCount()-1);
        coupon.setReceiveCount(coupon.getReceiveCount()==null?1:coupon.getReceiveCount()+1);
        couponMapper.updateByPrimaryKey(coupon);
         */
        if(0 == smsCouponMapper.updateCountAndReceiveCountByPrimaryKey(couponId)){
            log.warn("优惠券{}已派完，用户{}-{}无法领取，数据回滚",couponId,memberId,nickName);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CommonResult.failed("优惠券已经领完了");
        }
        return CommonResult.success(null,"领取成功");
    }


    /**
     * 16位优惠码生成：时间戳后8位+4位随机数+用户id后4位
     */
    private String generateCouponCode(Long memberId) {
        StringBuilder sb = new StringBuilder();
        Long currentTimeMillis = System.currentTimeMillis();
        String timeMillisStr = currentTimeMillis.toString();
        sb.append(timeMillisStr.substring(timeMillisStr.length() - 8));
        for (int i = 0; i < 4; i++) {
            sb.append(new Random().nextInt(10));
        }
        String memberIdStr = memberId.toString();
        if (memberIdStr.length() <= 4) {
            sb.append(String.format("%04d", memberId));
        } else {
            sb.append(memberIdStr.substring(memberIdStr.length()-4));
        }
        return sb.toString();
    }

    @Override
    public List<SmsCouponHistory> listCoupons(Integer useStatus, Long memberId) {

        SmsCouponHistoryExample couponHistoryExample = new SmsCouponHistoryExample();
        SmsCouponHistoryExample.Criteria criteria = couponHistoryExample.createCriteria();
        criteria.andMemberIdEqualTo(memberId);
        if(useStatus!=null){
            criteria.andUseStatusEqualTo(useStatus);
        }
        return smsCouponHistoryMapper.selectByExample(couponHistoryExample);
    }

    @Override
    public List<SmsCouponHistoryDetail> listCart(List<CartPromotionItem> cartItemList, Integer type, Long memberId) {

        Date now = new Date();
        //获取该用户所有优惠券
        List<SmsCouponHistoryDetail> allList = smsCouponHistoryDao.getDetailList(memberId);
        //根据优惠券使用类型来判断优惠券是否可用
        List<SmsCouponHistoryDetail> enableList = new ArrayList<>();
        List<SmsCouponHistoryDetail> disableList = new ArrayList<>();
        for (SmsCouponHistoryDetail couponHistoryDetail : allList) {
            Integer useType = couponHistoryDetail.getCoupon().getUseType();
            BigDecimal minPoint = couponHistoryDetail.getCoupon().getMinPoint();
            Date endTime = couponHistoryDetail.getCoupon().getEndTime();
            if(useType.equals(ConstantPromotion.COUPON_USE_TYPE_GENERAL)){
                //0->全场通用
                //判断是否满足优惠起点
                //计算购物车商品的总价
                BigDecimal totalAmount = calcTotalAmount(cartItemList);
                if(now.before(endTime)&&totalAmount.subtract(minPoint).intValue()>=0){
                    enableList.add(couponHistoryDetail);
                }else{
                    disableList.add(couponHistoryDetail);
                }
            }else if(useType.equals(ConstantPromotion.COUPON_USE_TYPE_SPEC_KIND)){
                //1->指定分类
                //计算指定分类商品的总价
                List<Long> productCategoryIds = new ArrayList<>();
                for (SmsCouponProductCategoryRelation categoryRelation : couponHistoryDetail.getCategoryRelationList()) {
                    productCategoryIds.add(categoryRelation.getProductCategoryId());
                }
                BigDecimal totalAmount = calcTotalAmountByproductCategoryId(cartItemList,productCategoryIds);
                if(now.before(endTime)&&totalAmount.intValue()>0&&totalAmount.subtract(minPoint).intValue()>=0){
                    enableList.add(couponHistoryDetail);
                }else{
                    disableList.add(couponHistoryDetail);
                }
            }else if(useType.equals(ConstantPromotion.COUPON_USE_TYPE_SPEC_PRODUCT)){
                //2->指定商品
                //计算指定商品的总价
                List<Long> productIds = new ArrayList<>();
                for (SmsCouponProductRelation productRelation : couponHistoryDetail.getProductRelationList()) {
                    productIds.add(productRelation.getProductId());
                }
                BigDecimal totalAmount = calcTotalAmountByProductId(cartItemList,productIds);
                if(now.before(endTime)&&totalAmount.intValue()>0&&totalAmount.subtract(minPoint).intValue()>=0){
                    enableList.add(couponHistoryDetail);
                }else{
                    disableList.add(couponHistoryDetail);
                }
            }
        }
        if(type.equals(1)){
            return enableList;
        }else{
            return disableList;
        }
    }

    private BigDecimal calcTotalAmount(List<CartPromotionItem> cartItemList) {
        BigDecimal total = new BigDecimal("0");
        for (CartPromotionItem item : cartItemList) {
            BigDecimal realPrice = item.getPrice();
            if (null!=item.getReduceAmount()){
                realPrice = item.getPrice().subtract(item.getReduceAmount());
            }
            total=total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
        }
        return total;
    }

    private BigDecimal calcTotalAmountByproductCategoryId(List<CartPromotionItem> cartItemList,List<Long> productCategoryIds) {
        BigDecimal total = new BigDecimal("0");
        for (CartPromotionItem item : cartItemList) {
            if(productCategoryIds.contains(item.getProductCategoryId())){
                BigDecimal realPrice = item.getPrice().subtract(item.getReduceAmount());
                total=total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
            }
        }
        return total;
    }

    private BigDecimal calcTotalAmountByProductId(List<CartPromotionItem> cartItemList,List<Long> productIds) {
        BigDecimal total = new BigDecimal("0");
        for (CartPromotionItem item : cartItemList) {
            if(productIds.contains(item.getProductId())){
                BigDecimal realPrice = item.getPrice().subtract(item.getReduceAmount());
                total=total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
            }
        }
        return total;
    }

}
