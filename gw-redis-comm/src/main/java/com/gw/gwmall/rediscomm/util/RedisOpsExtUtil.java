package com.gw.gwmall.rediscomm.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisOpsExtUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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


    public <V> void batchPutListRight(String key, Collection<V> values){
        if(CollectionUtils.isEmpty(values)){
            log.warn("{}没有数据可放入Redis",key);
        }else{
            for (V value : values) {
                redisTemplate.opsForList().rightPush(key,value);
            }
        }
    }

    public <T> List<T> getListAll(String key,Class<?> T){
        Object s = redisTemplate.opsForList().range(key,0,-1);
        return (List<T>)s;
    }

    public <T> List<List<T>>getAllList(String key,Class<T> clazz){
        Object s = redisTemplate.opsForList().range(key,0,-1);
        return (List<List<T>>)s;
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

    public Boolean expire(String key,long timeout,TimeUnit unit){
        return redisTemplate.expire(key,timeout, unit);
    }

    public Boolean delete(String key){
        return redisTemplate.delete(key);
    }

    public Boolean hasKey(String key){
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
