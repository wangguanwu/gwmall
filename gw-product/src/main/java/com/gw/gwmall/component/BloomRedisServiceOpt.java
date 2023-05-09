package com.gw.gwmall.component;

import com.gw.gwmall.common.api.ResultCode;
import com.gw.gwmall.common.constant.RedisKeyPrefixConst;
import com.gw.gwmall.common.exception.GwRuntimeException;
import com.gw.gwmall.rediscomm.util.RedisCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author guanwu
 * @created on 2023-04-21 19:16:00
 * redis增加bloom插件版本布隆过滤器实现
 **/

@Slf4j
public class BloomRedisServiceOpt implements BloomFilterService {

    @Value("${bloomfilter.config.error-rate}")
    private String errorRate;

    @Value("${bloomfilter.config.capacity}")
    private String capacity;

    private final RedisTemplate<String, String> strRedisTemplate;

    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, (r) -> {
        final Thread thread = new Thread(r);
        thread.setName("init-bloom-filter-thread");
        return thread;
    });

    private final AtomicBoolean isLoadScript = new AtomicBoolean(false);

    private final DefaultRedisScript<String> initBloomScript = new DefaultRedisScript<>();
    private final DefaultRedisScript<Long> bloomExistScript = new DefaultRedisScript<>();
    private final DefaultRedisScript<Long> bloomAddScript = new DefaultRedisScript<>();
    private final DefaultRedisScript<String> testScript = new DefaultRedisScript<>();
    public static final String INIT_BLOOM_SCRIPT_PATH = "bloom_filter_init.lua";
    public static final String BLOOM_EXIST_SCRIPT_PATH = "bloom_filter_exist.lua";
    public static final String BLOOM_ADD_SCRIPT_PATH = "bloom_filter_add.lua";
    public static final String TEST_LUA = "test.lua";


    public BloomRedisServiceOpt(RedisTemplate<String, String> strRedisTemplate) {
        this.strRedisTemplate = strRedisTemplate;
    }

    @PostConstruct
    public void preloadScript() {
        if (isLoadScript.get()) {
            return;
        }
        loadScript(bloomAddScript, new ResourceScriptSource(new ClassPathResource(BLOOM_ADD_SCRIPT_PATH)),
                BLOOM_ADD_SCRIPT_PATH, Long.class);
        loadScript(bloomExistScript, new ResourceScriptSource(new ClassPathResource(BLOOM_EXIST_SCRIPT_PATH)),
                BLOOM_EXIST_SCRIPT_PATH, Long.class);
        loadScript(initBloomScript, new ResourceScriptSource(new ClassPathResource(INIT_BLOOM_SCRIPT_PATH)),
                BLOOM_EXIST_SCRIPT_PATH, String.class);
        loadScript(testScript, new ResourceScriptSource(new ClassPathResource(TEST_LUA)),
                TEST_LUA, String.class);
        isLoadScript.set(true);

        ensureBloomKeyExist(initBloomScript, Collections.singletonList(RedisKeyPrefixConst.PRODUCT_REDIS_BLOOM_FILTER),
                errorRate, capacity);
//        loadTest(testScript, Collections.singletonList(TEST_LUA),
//                errorRate, capacity);

    }

    private void ensureBloomKeyExist(DefaultRedisScript<String> redisScript, List<String> keys,
                                     String errorRate, String capacity) {
        Runnable initializeBloomKeyTask = () -> {
            String result = strRedisTemplate.execute(redisScript, keys, this.errorRate, this.capacity);
            log.info("执行命令:{},结果:{}", redisScript.getSha1(), result);
            if (RedisKeyPrefixConst.REDIS_OPERATION_SUCCESS.equals(result)) {
                log.info("执行初始化脚本成功:{}，sha1值为：{}", INIT_BLOOM_SCRIPT_PATH, redisScript.getSha1());
                return;
            }
            log.error("初始化[{}]失败,重新调度:{}", keys, redisScript.getSha1());
            initBloomKey(redisScript, keys, errorRate, capacity);
        };
        scheduledThreadPoolExecutor.schedule(initializeBloomKeyTask, 100, TimeUnit.MILLISECONDS);
    }

    private void initBloomKey(DefaultRedisScript<String> redisScript, List<String> keys,
                              String errorRate, String capacity) {
        ensureBloomKeyExist(redisScript, keys, errorRate, capacity);
    }


    private void loadTest(DefaultRedisScript<String> redisScript, List<String> keys,
                          String errorRate, String capacity) {
        String result = strRedisTemplate.execute(redisScript, keys, errorRate, capacity);
        if (result == null) {
            throw new GwRuntimeException(ResultCode.ILLEGAL_STATE_ERROR.getCode(),
                    ResultCode.ILLEGAL_STATE_ERROR.getMessage());
        }

        log.info("执行初始化脚本成功:{}, {}", result, TEST_LUA);
    }

    private void loadScript(DefaultRedisScript<?> redisScript, ScriptSource scriptSource,
                            String luaName, Class t) {
        redisScript.setScriptSource(scriptSource);
        redisScript.setResultType(t);
        Objects.requireNonNull(strRedisTemplate.getConnectionFactory()).getConnection()
                .scriptLoad(redisScript.getScriptAsString().getBytes(StandardCharsets.UTF_8));
        RedisCommonUtil.loadRedisScript(strRedisTemplate, redisScript, luaName);
    }

    /**
     * 根据给定的布隆过滤器添加值
     */
    @Override
    public <T> void addByBloomFilter(String key, T value) {
        strRedisTemplate.execute(bloomAddScript, Collections.singletonList(key), value);
    }

    /**
     * 根据给定的布隆过滤器判断值是否存在
     */
    @Override
    public <T> boolean includeByBloomFilter(String key, T value) {
        Long execute = strRedisTemplate.execute(bloomExistScript, Collections.singletonList(key),
                value);
        return execute != null && execute == 1;
    }
}
