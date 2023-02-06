package com.gw.gwmall.promotion.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class RedisDistrLock {

    private final static String UNLOCK_LUA_NAME = "Redis分布式锁解锁脚本";
    private final static String UNLOCK_LUA = "local result = redis.call('get', KEYS[1]);" +
            "if result == ARGV[1] then redis.call('del', KEYS[1]) " +
            "return 1 else return nil end";

    /* 当前线程的锁集合，处理锁的可重入*/
    private ThreadLocal<Map<String, Integer>> lockers = new ThreadLocal<>();
    /* 当前线程锁的key和value集合*/
    private ThreadLocal<Map<String, String>> values = new ThreadLocal<>();

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private AtomicBoolean isLoadScript = new AtomicBoolean(false);
    private DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();

    @PostConstruct
    public void loadScript(){
        if(isLoadScript.get()) return;
        redisScript.setScriptText(UNLOCK_LUA);
        redisScript.setResultType(Long.class);
        loadRedisScript(redisScript,UNLOCK_LUA_NAME);
        isLoadScript.set(true);
    }

    /**
     * 加载lua脚本到redis服务器
     * @param redisScript
     * @param luaName
     */
    private void loadRedisScript(DefaultRedisScript<Long> redisScript, String luaName) {
        try {
            List<Boolean> results = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection()
                    .scriptExists(redisScript.getSha1());
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

    /**
     * 尝试获取锁
     * @param key 锁的键
     * @return 是否成功获取锁
     */
    private Boolean tryLock(String key,long timeout) {

        /*缺省失效时间为5秒*/
        if(timeout <= 0) timeout = 5000;
        String value = getValueByKey(key);
        // 如果没有设置过值
        if (value == null) {
            // 锁的值使用UUID生成随机ID以保证值的唯一性
            value = UUID.randomUUID().toString();
            // 将新生成的值放入集合中
            values.get().put(key, value);
        }
        if (redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(timeout))) {
            return true;
        }
        boolean isLock;
        long currentTimeMillis = System.currentTimeMillis();
        // 一直阻塞，直到拿到锁
        while (true) {
            try {
                Thread.sleep(5);
                // 继续尝试获取锁
                isLock = this.tryLock(key,timeout);
                if (isLock) {
                    return true;
                }
                if (System.currentTimeMillis() > currentTimeMillis + 5000) {
                    return false;
                }
            } catch (InterruptedException e) {
                log.debug("拿锁的休眠等待被中断！");
            }
        }
    }

    /**
     * 尝试释放锁
     * @param key 锁的键
     * @return 是否成功释放锁
     */
    private boolean tryRelease(String key) {
        String[] keys = new String[]{key};
        String[] args = new String[]{getValueByKey(key)};
        // 释放锁
        Long result = (Long)redisTemplate.execute(redisScript, Collections.singletonList(key),getValueByKey(key));
        return result != null;
    }

    /**
     * 获取当前线程的锁
     * @param key 锁的键
     * @return 当前线程的锁和该锁的重入次数
     */
    private Integer getLockerCnt(String key) {
        // 获取当前线程的锁集合
        Map<String, Integer> map = lockers.get();
        // 如果集合不为空，返回key对应的值
        if (map != null) {
            return map.get(key);
        }
        lockers.set(new HashMap<>(4));
        return null;
    }

    /**
     * 获取锁对应的值
     * @param key 锁的键
     * @return 锁对应的值
     */
    private String getValueByKey(String key) {
        // 获取当前线程的锁和对应值的键值对集合
        Map<String, String> map = values.get();
        // 如果集合不为空，返回key对应的值
        if (map != null) {
            return map.get(key);
        }
        values.set(new HashMap<>(4));
        return null;
    }

    /**
     * 加可重入锁
     * @param key 锁的键
     * @return 是否成功
     */
    public boolean lock(String key,long timeout){
        Integer refCnt = getLockerCnt(key);
        if (refCnt != null) {
            // 如果锁已持有，则锁的引用计数加1
            lockers.get().put(key, refCnt + 1);
            return true;
        }
        // 尝试加锁
        boolean ok = this.tryLock(key,timeout);
        // 如果加锁失败，则返回
        if (!ok) {
            return false;
        }
        // 加锁成功，引用计数设置为1
        lockers.get().put(key, 1);
        return true;
    }

    /**
     * 释放可重入锁
     * @param key 锁的键
     * @return 是否成功
     */
    public boolean unlock(String key) {
        Integer refCnt = getLockerCnt(key);
        // 当前未持有锁
        if (refCnt == null) {
            return false;
        }
        // 锁的引用数减1
        refCnt --;
        // 引用计数大于0，说明还持有锁
        if (refCnt > 0) {
            lockers.get().put(key, refCnt);
        } else {
            // 否则从锁集合中删除该键，并释放锁
            lockers.get().remove(key);
            return this.tryRelease(key);
        }
        return true;
    }

}
