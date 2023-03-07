package com.gw.gwmall.search.service.impl;

import com.gw.gwmall.search.dao.EsProductDao;
import com.gw.gwmall.search.domain.EsProduct;
import com.gw.gwmall.search.repository.EsProductRepository;
import com.gw.gwmall.search.service.EsProductDataService;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 商品搜索管理Service实现类
 */
@Service
public class EsProductDataServiceImpl implements EsProductDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EsProductDataServiceImpl.class);

    @Qualifier("restHighLevelClient")
    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private EsProductRepository productRepository;

    @Autowired
    private EsProductDao esProductDao;

    @Override
    public EsProduct create(Long id) {
        EsProduct esProduct = new EsProduct();
        esProduct.setId(id);
        esProduct.setName("测试产品:" + id);
        esProduct.setBrandId(1L);
        return productRepository.save(esProduct);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);

    }

    @Override
    public void delete(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        List<EsProduct> list = new ArrayList<>();
        for (Long id : ids) {
            EsProduct esProduct = new EsProduct();
            esProduct.setId(id);
            list.add(esProduct);
        }
        productRepository.deleteAll(list);
    }

    @Override
    public Page<EsProduct> search(String keyword, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return productRepository.findByNameOrSubTitleOrKeywords(keyword, keyword, keyword, pageable);
    }

    @Override
    public List<EsProduct> uploadAllProduct() {
        List<EsProduct> allProductList = esProductDao.getAllProductList();
        Iterable<EsProduct> esProducts = productRepository.saveAll(allProductList);
        List<EsProduct> res = new ArrayList<>();
        esProducts.forEach(res::add);
        return res;
    }
}
