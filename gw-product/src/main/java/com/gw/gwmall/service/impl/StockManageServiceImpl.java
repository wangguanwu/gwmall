package com.gw.gwmall.service.impl;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.dao.FlashPromotionProductDao;
import com.gw.gwmall.domain.CartPromotionItem;
import com.gw.gwmall.domain.PmsProductParam;
import com.gw.gwmall.domain.StockChanges;
import com.gw.gwmall.mapper.PmsSkuStockMapper;
import com.gw.gwmall.mapper.SmsFlashPromotionProductRelationMapper;
import com.gw.gwmall.model.PmsSkuStockExample;
import com.gw.gwmall.model.SmsFlashPromotionProductRelation;
import com.gw.gwmall.rediscomm.util.RedisOpsExtUtil;
import com.gw.gwmall.service.PmsProductService;
import com.gw.gwmall.service.StockManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;



@Service
@Slf4j
public class StockManageServiceImpl implements StockManageService {

    @Autowired
    private PmsSkuStockMapper skuStockMapper;

    @Autowired
    private SmsFlashPromotionProductRelationMapper flashPromotionProductRelationMapper;

    @Autowired
    private FlashPromotionProductDao flashPromotionProductDao;

    @Autowired
    private PmsProductService productService;

    @Autowired
    private RedisOpsExtUtil redisOpsUtil;

    @Override
    public Integer incrStock(Long productId, Long skuId, Integer quanlity, Integer miaosha, Long flashPromotionRelationId) {
        return null;
    }

    @Override
    public Integer descStock(Long productId, Long skuId, Integer quanlity, Integer miaosha, Long flashPromotionRelationId) {
        return null;
    }

    /**
     * 获取产品库存
     * @param productId
     * @param flashPromotionRelationId
     * @return
     */
    @Override
    public CommonResult<Integer> selectStock(Long productId, Long flashPromotionRelationId) {

        SmsFlashPromotionProductRelation miaoshaStock = flashPromotionProductRelationMapper.selectByPrimaryKey(flashPromotionRelationId);
        if(ObjectUtils.isEmpty(miaoshaStock)){
            return CommonResult.failed("不存在该秒杀商品！");
        }

        return CommonResult.success(miaoshaStock.getFlashPromotionCount());
    }

    /*库存锁定,需要同时扣减商品库存和增加锁定库存，原来的实现：
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(.......);
            skuStock.setLockStock(skuStock.getLockStock() + cartPromotionItem.getQuantity());
            skuStockMapper.updateByPrimaryKeySelective(skuStock);
    这里是先查再减，会有并发问题。
    * 实际扣减时也要判断商品库存是否足够扣减，否则会出现超卖*/
    @Override
    public CommonResult<Boolean>  lockStock(List<CartPromotionItem> cartPromotionItemList) {
        try {
            for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
                PmsSkuStockExample pmsSkuStockExample = new PmsSkuStockExample();
                pmsSkuStockExample.createCriteria()
                        .andIdEqualTo(cartPromotionItem.getProductSkuId())
                        .andStockGreaterThanOrEqualTo(cartPromotionItem.getQuantity());
                skuStockMapper.lockStockByExample(cartPromotionItem.getQuantity(),pmsSkuStockExample);
            }
            /*这里我们做了简单化处理，认为所有商品的库存锁定在业务上都可以成功，
            也就是商品库存一定足够扣减。
            实际要检查SQL操作返回行数，以供后续处理每个商品的锁定结果*/
            return CommonResult.success(true);
        }catch (Exception e) {
            log.error("锁定库存失败...{}",e);
            return CommonResult.failed();
        }
    }

    /*订单支付后，实际扣减库存*/
    @Override
    public CommonResult<Integer> reduceStock(List<StockChanges> stockChangesList) {
        try {
//            for (StockChanges changesProduct : stockChangesList) {
//                PmsSkuStockExample pmsSkuStockExample = new PmsSkuStockExample();
//                pmsSkuStockExample.createCriteria()
//                        .andIdEqualTo(changesProduct.getProductSkuId());
//                skuStockMapper.reduceStockByExample(changesProduct.getChangesCount(),pmsSkuStockExample);
//            }
            int result = skuStockMapper.updateSkuStock(stockChangesList);
            return CommonResult.success(result);
        }catch (Exception e) {
            log.error("订单支付后扣减库存失败...{}",e);
            return CommonResult.failed();
        }
    }

    /*订单取消后，恢复库存*/
    @Override
    public CommonResult<Boolean>  recoverStock(List<StockChanges> stockChangesList) {
        try {
            for (StockChanges changesProduct : stockChangesList) {
                PmsSkuStockExample pmsSkuStockExample = new PmsSkuStockExample();
                pmsSkuStockExample.createCriteria()
                        .andIdEqualTo(changesProduct.getProductSkuId());
                skuStockMapper.recoverStockByExample(changesProduct.getChangesCount(),pmsSkuStockExample);
            }
            return CommonResult.success(true);
        }catch (Exception e) {
            log.error("恢复库存失败...{}",e);
            return CommonResult.failed();
        }
    }

    //验证秒杀时间
    private boolean volidateMiaoShaTime(PmsProductParam product) {
        //当前时间
        Date now = new Date();
        //todo 查看是否有秒杀商品,秒杀商品库存
        if(product.getFlashPromotionStatus() != 1
                || product.getFlashPromotionEndDate() == null
                || product.getFlashPromotionStartDate() == null
                || now.after(product.getFlashPromotionEndDate())
                || now.before(product.getFlashPromotionStartDate())){
            return false;
        }
        return true;
    }

}
