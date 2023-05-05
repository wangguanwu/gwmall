package com.gw.gwmall.rediscomm.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @author guanwu
 * @created on 2023-03-01 13:15:09
 **/

@Slf4j
public class RedisCommonUtil {

    public static RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // 序列化工具
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer
                = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(om.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    public static StringRedisTemplate createStrRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(factory);
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }

    /**
     * 加载lua脚本到redis服务器
     * @param redisScript
     * @param luaName
     */
    public static<T> void loadRedisScript(RedisTemplate<String,?> redisTemplate, DefaultRedisScript<T> redisScript, String luaName) {
        try {
            List<Boolean> results = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection()
                    .scriptExists(redisScript.getSha1());
            log.info("脚本sha1值为{}, 脚本名称:{}", redisScript.getSha1(), luaName);
            assert results != null;
            if (Boolean.FALSE.equals(results.get(0))) {
                String sha = redisTemplate.getConnectionFactory().getConnection()
                        .scriptLoad(redisScript.getScriptAsString().getBytes(StandardCharsets.UTF_8));
                log.info("预加载lua脚本成功：{}, sha=[{}]", luaName, sha);
            }
        } catch (Exception e) {
            log.error("预加载lua脚本异常：{}", luaName, e);
        }
    }
}
