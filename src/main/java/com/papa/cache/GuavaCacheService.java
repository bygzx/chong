package com.papa.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author eric
 * @date 2019/7/11 15:53
 **/
@Service
@Slf4j
public class GuavaCacheService {
    //创建静态缓存
    private static volatile Map<String,Object> stringCacheForTest = new LinkedHashMap<>();
    //删除、读取锁
    //private boolean lock=false;

    //单例模式获取缓存
    public static Map<String,Object>getStringCache(){
        return stringCacheForTest;
    }

    public void put(String key,Object value){
        //if(stringCacheForTest.size()<10) {
            stringCacheForTest.put(key, value);
            log.info("新增元素到队尾----key:{},value:{}", key, value.toString());
        /*}else{
            log.info("队列已满，不插了");
        }*/
    }

    public  Object getCacheByKey(String key){
        return stringCacheForTest.get(key);
    }
    //获取map第一个元素
    public Object getFirstCacheValue(){
        if(stringCacheForTest.size()>0) {
            /*if(lock) {
                lock = true;*/
                return stringCacheForTest.entrySet().iterator().next();
            /*}else{
                return null;
            }*/
        }else {
            return null;
        }
    }
    //打印所有元素
    public void printAllCacheValue(){
        Map<String,Object> map = stringCacheForTest;
        if(map!=null){
            for (Map.Entry<String,Object> entry : map.entrySet()) {
                log.info("遍历所有元素：{}",entry.toString());
            }
        }
    }

    public void deleteFristCacheValue(){
        Map.Entry<String,Object> entry = (Map.Entry<String,Object>)getFirstCacheValue();
        stringCacheForTest.remove(entry.getKey());
        //lock = false;
        log.info("踢出第一个元素:{}",entry.toString());
    }
}
