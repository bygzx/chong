package com.papa.cache;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author eric
 * @date 2019/7/11 15:53
 **/
@Service
@Slf4j
public class GuavaCacheService {
    //创建静态缓存
    private static volatile Map<String,Object> stringCacheForTest = new LinkedHashMap<>();
    private static volatile Map<String,Object> stringCacheForMin = new ConcurrentHashMap<>();
    //删除、读取锁
    //private boolean lock=false;

    //单例模式获取缓存
    public static Map<String,Object>getStringCache(){
        return stringCacheForTest;
    }

    public static Map<String,Object>getStringCache(int type){
        switch (type){
            case 1:return stringCacheForTest;
            case 2:return stringCacheForMin;
        }
        return stringCacheForTest;
    }

    public void put(String key,Object value){
            stringCacheForTest.put(key, value);
    }

    public void put(int type,String key,Object value){
        switch (type){
            case 1:stringCacheForTest.put(key, value);break;
            case 2:stringCacheForMin.put(key, value);break;
        }
    }

    public  Object getCacheByKey(String key){
        return stringCacheForTest.get(key);
    }
    public  Object getCacheByKey(int type,String key){
        switch (type){
            case 1:return stringCacheForTest.get(key);
            case 2:return stringCacheForMin.get(key);
            default:return null;
        }
    }
    //获取map第一个元素
    public Object getFirstCacheValue(){
        if(stringCacheForTest.size()>0) {
                return stringCacheForTest.entrySet().iterator().next();
        }else {
            return null;
        }
    }

    public Object getFirstCacheValue(int type){
        switch (type){
            case 1:{
                if(stringCacheForTest.size()>0) {
                    return stringCacheForTest.entrySet().iterator().next();
                }else {
                    return null;
                }
            }
            case 2:{
                if(stringCacheForMin.size()>0) {
                    return stringCacheForMin.entrySet().iterator().next();
                }else {
                    return null;
                }
            }
            default:return null;
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

    public void deleteItemByKey(int type,String key){
        switch (type){
            case 1:stringCacheForTest.remove(key);break;
            case 2:stringCacheForMin.remove(key);break;
        }
    }

    public void printAllCacheValue(int type){
        switch (type){
            case 1:{
                Map<String,Object> map = stringCacheForTest;
                if(map!=null){
                    for (Map.Entry<String,Object> entry : map.entrySet()) {
                        log.info("遍历所有元素：{}",entry.toString());
                    }
                }
                break;
            }
            case 2:{
                Map<String,Object> map = stringCacheForMin;
                if(map!=null){
                    for (Map.Entry<String,Object> entry : map.entrySet()) {
                        log.info("遍历所有元素：{}",entry.toString());
                    }
                }
                break;
            }
        }
    }

    public void deleteFristCacheValue(){
        Map.Entry<String,Object> entry = (Map.Entry<String,Object>)getFirstCacheValue();
        stringCacheForTest.remove(entry.getKey());
        //lock = false;
        //log.info("踢出第一个元素:{}",entry.toString());
    }

    public void deleteFristCacheValue(int type){

        switch (type){
            case 1:{
                Map.Entry<String,Object> entry = (Map.Entry<String,Object>)getFirstCacheValue();
                stringCacheForTest.remove(entry.getKey());
                //log.info("踢出第一个元素:{}",entry.toString());
                break;
            }
            case 2:{
                Map.Entry<String,Object> entry = (Map.Entry<String,Object>)getFirstCacheValue(2);
                stringCacheForMin.remove(entry.getKey());
                //log.info("踢出第一个元素:{}",entry.toString());
                break;
            }
        }
    }
}
