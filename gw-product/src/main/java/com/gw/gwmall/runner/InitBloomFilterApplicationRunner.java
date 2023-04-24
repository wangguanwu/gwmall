package com.gw.gwmall.runner;

import com.gw.gwmall.common.constant.RedisKeyPrefixConst;
import com.gw.gwmall.component.BloomFilterService;
import com.gw.gwmall.service.PmsProductService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author guanwu
 * @created on 2023-04-23 09:36:59
 **/

@Component
@Slf4j
public class InitBloomFilterApplicationRunner implements ApplicationRunner {

    @Resource
    private PmsProductService productService;

    @Resource
    private BloomFilterService bloomFilterService;

    @Resource
    private RedissonClient redissonClient;

    private static final String BLOOM_FILTER_INIT_LOCK_KEY = "mall:bloom:init:lock";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread t = new Thread(() -> {
            RLock lock = redissonClient.getLock(BLOOM_FILTER_INIT_LOCK_KEY);
            boolean isLock = false;
            try {
                isLock = lock.tryLock(2, TimeUnit.SECONDS);
                if (isLock) {
                    initBloomFilter();
                }
            } catch (InterruptedException e) {
               log.error(e.getMessage(), e);
            } finally {
                if (isLock) {
                    lock.unlock();
                }
            }
        });
        t.setName("init-bloom-filter-thread");
        t.start();
    }

    private void initBloomFilter() {
        List<Long> list = productService.getAllProductId();
        log.info("加载产品到布隆过滤器当中,size:{}", list.size());
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(item -> {
                bloomFilterService.addByBloomFilter(RedisKeyPrefixConst.PRODUCT_REDIS_BLOOM_FILTER, item + "");
            });
        }
    }
}
