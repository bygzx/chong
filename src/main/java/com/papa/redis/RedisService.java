package com.papa.redis;

import com.papa.exception.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author bygzx
 * @date 2019/5/14 14:21
 **/
@Service
@Slf4j
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String LOCK_PREFIX = "redis_lock";
    public static final int LOCK_EXPIRE = 3000; // ms  3秒
    /**
     * 写入缓存
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 写入缓存设置时效时间
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 批量删除对应的value
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }
    /**
     * 删除对应的value
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }
    /**
     * 判断缓存中是否有对应的value
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }
    /**
     * 读取缓存
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }
    /**
     * 哈希 添加
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, Object hashKey, Object value){
        String lockKey = "."+key+"."+hashKey;
        try {
            int i=0;
            boolean lock = true;
            while(!lock(lockKey)) {
                i++;
                lock = false;
                if(i>=4){
                    log.error("【hmSet】 3次都为获得锁,抛弃 数据：key：{}，hashKey：{}",key,hashKey);
                    break;
                }
                Thread.sleep(200);
            }
            if(lock) {
                HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
                hash.put(key, hashKey, value);
            }
        }catch (Exception e){
            log.error("【hmSet】  获取锁失败");
        }finally {
            releaseLock(lockKey);
            log.info("【hmSet】 解锁：key：{}，hashKey：{}",key,hashKey);
        }
    }

    /**
     * 哈希获取数据
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, Object hashKey){
        String lockKey = "."+key+"."+hashKey;
        Object o = null;
        try {
            int i=0;
            boolean lock = true;
            while(!lock(lockKey)) {
                i++;
                lock = false;
                if(i>=4){
                    log.error("【hmGet】 3次都为获得锁,抛弃 数据：key：{}，hashKey：{}",key,hashKey);
                    break;
                }
                Thread.sleep(200);
            }
            if(lock) {
                HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
                o = hash.get(key, hashKey);
            }
        }catch (Exception e){
            log.error("【hmGet】  获取锁失败");
        }finally {
            releaseLock(lockKey);
            log.info("【hmGet】 解锁：key：{}，hashKey：{}",key,hashKey);
            return o;
        }
    }

    public Map<Object,Object> hmGetAll(String key){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.entries(key);
    }

    /**
     * 列表添加
     * @param k
     * @param v
     */
    public void lPush(String k,Object v){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(k,v);
    }

    /**
     * 列表获取
     * @param k
     * @param l
     * @param l1
     * @return
     */
    public List<Object> lRange(String k, long l, long l1){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k,l,l1);
    }

    /**
     * 集合添加
     * @param key
     * @param value
     */
    public void add(String key,Object value){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key,value);
    }

    /**
     * 集合获取
     * @param key
     * @return
     */
    public Set<Object> getMembers(String key){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     * @param key
     * @param value 不能相同，相同就会覆盖原来的
     * @param timeStemp
     */
    public void zAdd(String key,Object value,long timeStemp){
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key,value,timeStemp);
    }

    /**
     * 有序集合获取
     * @param key
     * @param timeStemp
     * @param timeStemp1
     * @return
     */
    public Set<Object> rangeByScore(String key,long timeStemp,long timeStemp1){
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, timeStemp, timeStemp1);
    }

    public void zDelete(String key,long timeStemp,long timeStemp1){
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        Set<Object> set = zset.rangeByScore(key, timeStemp, timeStemp1);
        List<String> value = new ArrayList<>();
        if(set!=null && set.size()>0){
            for(Object o:set){
                value.add(String.valueOf(o));
                zset.remove(key,String.valueOf(o));
            }

        }
        //zset.removeRange(key,timeStemp,timeStemp1);
    }

    /**
     *  最终加强分布式锁
     *
     * @param key key值
     * @return 是否获取到
     */
    public boolean lock(String key){
        String lock = LOCK_PREFIX + key;
        // 利用lambda表达式
        return (Boolean) redisTemplate.execute((RedisCallback) connection -> {

            long expireAt = System.currentTimeMillis() + LOCK_EXPIRE + 1;
            Boolean acquire = connection.setNX(lock.getBytes(), String.valueOf(expireAt).getBytes());


            if (acquire) {
                return true;
            } else {

                byte[] value = connection.get(lock.getBytes());

                if (Objects.nonNull(value) && value.length > 0) {

                    long expireTime = Long.parseLong(new String(value));

                    if (expireTime < System.currentTimeMillis()) {
                        // 如果锁已经过期
                        byte[] oldValue = connection.getSet(lock.getBytes(), String.valueOf(System.currentTimeMillis() + LOCK_EXPIRE + 1).getBytes());
                        // 防止死锁
                        return Long.parseLong(new String(oldValue)) < System.currentTimeMillis();
                    }
                }
            }
            return false;
        });
    }

    /**
     * 删除锁
     *
     * @param key
     */
    public void releaseLock(String key) {
        String lock = LOCK_PREFIX + key;
        redisTemplate.delete(lock);
    }

}
