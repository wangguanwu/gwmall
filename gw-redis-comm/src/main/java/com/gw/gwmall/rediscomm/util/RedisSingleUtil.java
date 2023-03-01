package com.gw.gwmall.rediscomm.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisSingleUtil {

    @Autowired
    @Qualifier("redisSingleTemplate")
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init(){
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        RedisConnection conn = RedisConnectionUtils.getConnection(factory);
        log.info("Redis服务器信息：{}",conn.info().toString());
    }

    public void set(String key,Object value){
        redisTemplate.opsForValue().set(key,value);
    }

    public void set(String key, Object value, long timeout, TimeUnit unit){
        redisTemplate.opsForValue().set(key,value,timeout,unit);
    }

    public <V> void putListAllRight(String key, Collection<V> values){
        if(CollectionUtils.isEmpty(values)){
            log.warn("{}没有数据可放入Redis",key);
        }else{
            redisTemplate.opsForList().rightPushAll(key,values);
        }
    }

    public <T> T getListAll(String key,Class<?> T){
        return (T)redisTemplate.opsForList().range(key,0,-1);
    }

    public <T> T get(String key,Class<?> T){
        return (T)redisTemplate
                .opsForValue().get(key);
    }

    public String get(String key){
        return (String) redisTemplate
                .opsForValue().get(key);
    }

    public Long decr(String key){
        return redisTemplate
                .opsForValue().decrement(key);
    }

    public Long decr(String key,long delta){
        return redisTemplate
                .opsForValue().decrement(key,delta);
    }

    public Long incr(String key){
        return redisTemplate
                .opsForValue().increment(key);
    }

    public Long incr(String key,long delta){
        return redisTemplate
                .opsForValue().increment(key,delta);
    }

    public boolean expire(String key,long timeout,TimeUnit unit){
        return redisTemplate.expire(key,timeout, unit);
    }

    public boolean delete(String key){
        return redisTemplate.delete(key);
    }

    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * 发布channel信息
     * @param channel
     * @param message
     */
    public void publish(String channel,Object message){
        redisTemplate.convertAndSend(channel,message);
    }

}
