package com.gw.gwmall.promotion.service.impl;

import com.github.pagehelper.PageHelper;

import com.gw.gwmall.FlashPromotionProduct;
import com.gw.gwmall.model.PmsBrand;
import com.gw.gwmall.model.PmsProduct;
import com.gw.gwmall.promotion.clientapi.PmsProductClientApi;
import com.gw.gwmall.promotion.config.PromotionRedisKey;
import com.gw.gwmall.promotion.dao.FlashPromotionProductDao;
import com.gw.gwmall.promotion.domain.FlashPromotionParam;
import com.gw.gwmall.promotion.domain.HomeContentResult;
import com.gw.gwmall.promotion.mapper.*;
import com.gw.gwmall.promotion.model.*;
import com.gw.gwmall.promotion.service.HomePromotionService;
import com.gw.gwmall.promotion.util.RedisDistrLock;
import com.gw.gwmall.rediscomm.util.RedisOpsExtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 首页促销内容Service实现类
 */
@Slf4j
@Service
public class HomePromotionServiceImpl implements HomePromotionService {
    @Autowired
    private SmsHomeAdvertiseMapper advertiseMapper;
    @Autowired
    private SmsHomeBrandMapper smsHomeBrandMapper;
    @Autowired
    private SmsHomeNewProductMapper smsHomeNewProductMapper;
    @Autowired
    private SmsHomeRecommendProductMapper smsHomeRecommendProductMapper;
    @Autowired
    private PmsProductClientApi pmsProductClientApi;
    @Autowired
    private FlashPromotionProductDao flashPromotionProductDao;
    @Autowired
    private PromotionRedisKey promotionRedisKey;
    @Autowired
    private RedisOpsExtUtil redisOpsExtUtil;
    @Autowired
    private RedisDistrLock redisDistrLock;
    @Autowired
    private SmsFlashPromotionMapper smsFlashPromotionMapper;

    @Value("#{'${secKillServerList}'.split(',')}")
    private List<String> secKillServerList;

    @Override
    public HomeContentResult content(int getType) {
        HomeContentResult result = new HomeContentResult();
        if(ConstantPromotion.HOME_GET_TYPE_ALL == getType
                ||ConstantPromotion.HOME_GET_TYPE_BARND == getType){
            //获取推荐品牌
            getRecommendBrand(result);
        }
        if(ConstantPromotion.HOME_GET_TYPE_ALL == getType
                ||ConstantPromotion.HOME_GET_TYPE_NEW == getType){
            getRecommendProducts(result);
        }
        if(ConstantPromotion.HOME_GET_TYPE_ALL == getType
                ||ConstantPromotion.HOME_GET_TYPE_HOT == getType){
            getHotProducts(result);
        }
        if(ConstantPromotion.HOME_GET_TYPE_ALL == getType
                ||ConstantPromotion.HOME_GET_TYPE_AD == getType){
            //获取首页广告
            result.setAdvertiseList(getHomeAdvertiseList());
        }
        return result;
    }

    /*获取秒杀内容*/
    @Override
    public List<FlashPromotionProduct> secKillContent(long secKillId, int status) {
        PageHelper.startPage(1, 8);
        Long secKillIdL = -1 == secKillId ? null : secKillId;
        /*获得秒杀相关的活动信息*/
        FlashPromotionParam flashPromotionParam = flashPromotionProductDao.getFlashPromotion(secKillIdL,status);
        if (flashPromotionParam == null || CollectionUtils.isEmpty(flashPromotionParam.getRelation())) {
            return null;
        }
        /*获得秒杀相关的商品信息,map用来快速寻找秒杀商品的限购信息*/
        List<Long> productIds = new ArrayList<>();
        Map<Long, SmsFlashPromotionProductRelation> temp = new HashMap<>();
        flashPromotionParam.getRelation().stream().forEach(item -> {
            productIds.add(item.getProductId());
            temp.put(item.getProductId(),item);
        });
        PageHelper.clearPage();
        List<PmsProduct> secKillProducts = pmsProductClientApi.getProductBatch(productIds);
        /*拼接前端需要的内容*/
        List<FlashPromotionProduct> flashPromotionProducts = new ArrayList<>();
        int loop = 0;
        int serverSize = secKillServerList.size();
        for(PmsProduct product : secKillProducts){
            FlashPromotionProduct flashPromotionProduct = new FlashPromotionProduct();
            BeanUtils.copyProperties(product,flashPromotionProduct);
            Long productId = product.getId();
            SmsFlashPromotionProductRelation item = temp.get(productId);
            flashPromotionProduct.setFlashPromotionCount(item.getFlashPromotionCount());
            flashPromotionProduct.setFlashPromotionPrice(item.getFlashPromotionPrice());
            flashPromotionProduct.setFlashPromotionLimit(item.getFlashPromotionLimit());
            flashPromotionProduct.setRelationId(item.getId());
            Long flashPromotionId = item.getFlashPromotionId();
            flashPromotionProduct.setFlashPromotionId(flashPromotionId);
            flashPromotionProduct.setFlashPromotionStartDate(flashPromotionParam.getStartDate());
            flashPromotionProduct.setFlashPromotionEndDate(flashPromotionParam.getEndDate());
            String url = secKillServerList.get(loop % serverSize)+"/product?"+"flashPromotionId="+flashPromotionId
                    +"&promotionProductId="+productId;
            flashPromotionProduct.setSecKillServer(url);
            flashPromotionProducts.add(flashPromotionProduct);
            loop++;
        }
        return flashPromotionProducts;
    }

    @Override
    public int turnOnSecKill(long secKillId,int status) {
        SmsFlashPromotion smsFlashPromotion = new SmsFlashPromotion();
        smsFlashPromotion.setId(secKillId);
        smsFlashPromotion.setStatus(status);
        return smsFlashPromotionMapper.updateByPrimaryKeySelective(smsFlashPromotion);
    }

    /*获取推荐品牌*/
    private void getRecommendBrand(HomeContentResult result) {
        final String brandKey = promotionRedisKey.getBrandKey();
        List<PmsBrand> recommendBrandList = redisOpsExtUtil.getListAll(brandKey, PmsBrand.class);
        if (CollectionUtils.isEmpty(recommendBrandList)) {
            redisDistrLock.lock(promotionRedisKey.getDlBrandKey(),promotionRedisKey.getDlTimeout());
            try {
                PageHelper.startPage(0,ConstantPromotion.HOME_RECOMMEND_PAGESIZE,"sort desc");
                SmsHomeBrandExample example = new SmsHomeBrandExample();
                example.or().andRecommendStatusEqualTo(ConstantPromotion.HOME_PRODUCT_RECOMMEND_YES);
                List<Long> smsHomeBrandIds = smsHomeBrandMapper.selectBrandIdByExample(example);
//                pmsProductFeignApi.getHomeSecKillProductList();
//                log.info("---------------------------");
                recommendBrandList = pmsProductClientApi.getRecommandBrandList(smsHomeBrandIds);
                redisOpsExtUtil.putListAllRight(brandKey,recommendBrandList);
            } finally {
                redisDistrLock.unlock(promotionRedisKey.getDlBrandKey());
            }
            result.setBrandList(recommendBrandList);
            log.info("品牌推荐信息存入缓存，键{}" ,brandKey);
        } else {
            log.info("品牌推荐信息已在缓存，键{}" ,brandKey);
            result.setBrandList(recommendBrandList);
        }
    }

    /*获取人气推荐产品*/
    private void getRecommendProducts(HomeContentResult result) {
        final String recProductKey = promotionRedisKey.getRecProductKey();
        List<PmsProduct> recommendProducts = redisOpsExtUtil.getListAll(recProductKey, PmsProduct.class);
        if (CollectionUtils.isEmpty(recommendProducts)) {
            redisDistrLock.lock(promotionRedisKey.getDlRecProductKey(),promotionRedisKey.getDlTimeout());
            try {
                PageHelper.startPage(0,ConstantPromotion.HOME_RECOMMEND_PAGESIZE,"sort desc");
                SmsHomeRecommendProductExample example2 = new SmsHomeRecommendProductExample();
                example2.or().andRecommendStatusEqualTo(ConstantPromotion.HOME_PRODUCT_RECOMMEND_YES);
                List<Long> recommendProductIds = smsHomeRecommendProductMapper.selectProductIdByExample(example2);
                recommendProducts = pmsProductClientApi.getProductBatch(recommendProductIds);
                redisOpsExtUtil.putListAllRight(recProductKey,recommendProducts);
            } finally {
                redisDistrLock.unlock(promotionRedisKey.getDlRecProductKey());
            }
            log.info("人气推荐商品信息存入缓存，键{}" ,recProductKey);
            result.setHotProductList(recommendProducts);
        } else {
            log.info("人气推荐商品信息已在缓存，键{}" ,recProductKey);
            result.setHotProductList(recommendProducts);
        }
    }

    /*获取新品推荐产品*/
    private void getHotProducts(HomeContentResult result){
        final String newProductKey = promotionRedisKey.getNewProductKey();
        List<PmsProduct> newProducts = redisOpsExtUtil.getListAll(newProductKey, PmsProduct.class);
        if(CollectionUtils.isEmpty(newProducts)){
            redisDistrLock.lock(promotionRedisKey.getDlNewProductKey(),promotionRedisKey.getDlTimeout());
            try {
                PageHelper.startPage(0,ConstantPromotion.HOME_RECOMMEND_PAGESIZE,"sort desc");
                SmsHomeNewProductExample example = new SmsHomeNewProductExample();
                example.or().andRecommendStatusEqualTo(ConstantPromotion.HOME_PRODUCT_RECOMMEND_YES);
                List<Long> newProductIds = smsHomeNewProductMapper.selectProductIdByExample(example);
                newProducts = pmsProductClientApi.getProductBatch(newProductIds);
                redisOpsExtUtil.putListAllRight(newProductKey,newProducts);
            } finally {
                redisDistrLock.unlock(promotionRedisKey.getDlNewProductKey());
            }
            log.info("新品推荐信息存入缓存，键{}" ,newProductKey);
            result.setNewProductList(newProducts);
        }else{
            log.info("新品推荐信息已在缓存，键{}" ,newProductKey);
            result.setNewProductList(newProducts);
        }
    }

    /*获取轮播广告*/
    private List<SmsHomeAdvertise> getHomeAdvertiseList() {
        final String homeAdvertiseKey = promotionRedisKey.getHomeAdvertiseKey();
        List<SmsHomeAdvertise> smsHomeAdvertises =
                redisOpsExtUtil.getListAll(homeAdvertiseKey, SmsHomeAdvertise.class);
        if(CollectionUtils.isEmpty(smsHomeAdvertises)){
            redisDistrLock.lock(promotionRedisKey.getDlHomeAdvertiseKey(),promotionRedisKey.getDlTimeout());
            try {
                SmsHomeAdvertiseExample example = new SmsHomeAdvertiseExample();
                Date now = new Date();
                example.createCriteria().andTypeEqualTo(ConstantPromotion.HOME_ADVERTISE_TYPE_APP)
                        .andStatusEqualTo(ConstantPromotion.HOME_ADVERTISE_STATUS_ONLINE)
                        .andStartTimeLessThan(now).andEndTimeGreaterThan(now);
                example.setOrderByClause("sort desc");
                smsHomeAdvertises = advertiseMapper.selectByExample(example);
                redisOpsExtUtil.putListAllRight(homeAdvertiseKey,smsHomeAdvertises);
            } finally {
                redisDistrLock.unlock(promotionRedisKey.getDlHomeAdvertiseKey());
            }
            log.info("轮播广告存入缓存，键{}" ,homeAdvertiseKey);
            return smsHomeAdvertises;
        }else{
            log.info("轮播广告已在缓存，键{}" ,homeAdvertiseKey);
            return smsHomeAdvertises;
        }
    }
}
