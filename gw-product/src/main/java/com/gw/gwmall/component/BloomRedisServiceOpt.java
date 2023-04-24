package com.gw.gwmall.component;

import com.gw.gwmall.common.api.ResultCode;
import com.gw.gwmall.common.exception.GwRuntimeException;
import com.gw.gwmall.rediscomm.util.RedisCommonUtil;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author guanwu
 * @created on 2023-04-21 19:16:00
 * redis增加bloom插件版本布隆过滤器实现
 **/

@Slf4j
public class BloomRedisServiceOpt implements BloomFilterService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final AtomicBoolean isLoadScript = new AtomicBoolean(false);

    private final DefaultRedisScript<Boolean> initBloomScript = new DefaultRedisScript<>();
    private final DefaultRedisScript<Boolean> bloomExistScript = new DefaultRedisScript<>();
    private final DefaultRedisScript<Boolean> bloomAddScript = new DefaultRedisScript<>();
    public static final String INIT_BLOOM_SCRIPT_PATH = "bloom_filter_init.lua";
    public static final String BLOOM_EXIST_SCRIPT_PATH = "bloom_filter_exist.lua";
    public static final String BLOOM_ADD_SCRIPT_PATH = "bloom_filter_add.lua";
    public static final String PRODUCT_BLOOM_FILTER = "bloom_filter_product";


    public BloomRedisServiceOpt(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void preloadScript() {
        if (isLoadScript.get()) {
            return;
        }
        loadScript(bloomAddScript, new ResourceScriptSource(new ClassPathResource(BLOOM_ADD_SCRIPT_PATH)),
                BLOOM_ADD_SCRIPT_PATH);
        loadScript(bloomExistScript, new ResourceScriptSource(new ClassPathResource(BLOOM_EXIST_SCRIPT_PATH)),
                BLOOM_EXIST_SCRIPT_PATH);
        loadScript(initBloomScript, new ResourceScriptSource(new ClassPathResource(INIT_BLOOM_SCRIPT_PATH)),
                INIT_BLOOM_SCRIPT_PATH);
        isLoadScript.set(true);

        ensureBloomKeyExist(initBloomScript, Collections.singletonList(PRODUCT_BLOOM_FILTER),
                Collections.EMPTY_LIST);

    }

    private void ensureBloomKeyExist(DefaultRedisScript<Boolean> redisScript,
                                     List<String> keys, List<String> values) {
        Boolean result = redisTemplate.execute(redisScript, keys, values);
        if (result == null || !result) {
            throw new GwRuntimeException(ResultCode.ILLEGAL_STATE_ERROR.getCode(),
                    ResultCode.ILLEGAL_STATE_ERROR.getMessage());
        }
        log.info("执行初始化脚本成功:{}", INIT_BLOOM_SCRIPT_PATH);

    }

    private void loadScript(DefaultRedisScript<Boolean> redisScript, ScriptSource scriptSource,
                             String luaName) {
        redisScript.setScriptSource(scriptSource);
        redisScript.setResultType(Boolean.class);
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection()
                .scriptLoad(redisScript.getScriptAsString().getBytes(StandardCharsets.UTF_8));
        RedisCommonUtil.loadRedisScript(redisTemplate, redisScript, luaName);
    }

    /**
     * 根据给定的布隆过滤器添加值
     */
    @Override
    public <T> void addByBloomFilter(String key, T value) {
       redisTemplate.execute(bloomAddScript, Collections.singletonList(key), Collections.singletonList(value));
    }

    /**
     * 根据给定的布隆过滤器判断值是否存在
     */
    @Override
    public <T> boolean includeByBloomFilter(String key, T value) {
        Boolean execute = redisTemplate.execute(bloomExistScript, Collections.singletonList(key),
                Collections.singletonList(value));
        return execute != null && execute;
    }
}
