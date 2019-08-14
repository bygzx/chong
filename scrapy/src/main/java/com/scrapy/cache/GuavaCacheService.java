package com.scrapy.cache;


import com.util.date.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
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
    private static volatile Map<String,Object> stringCacheForMin = new ConcurrentHashMap<>();
    //分钟缓存
    private static volatile Map<String,String> cacheForMin = new LinkedHashMap<>();

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,10, 30, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(50));

    public GuavaCacheService(){
        log.info("..................... init cacheForMin.....................");
        cacheForMin();
    }

    //自旋锁，为缓存加分钟
    private void cacheForMin(){
        long now = System.currentTimeMillis();
        now = DateUtils.transformToMinLong(now);
        cacheForMin.put(String.valueOf(now),String.valueOf(now));
        for(int i=240;i>=1;i--){
            long date = DateUtils.addMin(-i,now);
            date = DateUtils.transformToMinLong(date);
            cacheForMin.put(String.valueOf(date),String.valueOf(date));
        }
        //printAllCacheForMin(1);
        threadPoolExecutor.submit(() ->{subThread();});
    }
    private void subThread(){
        //自旋锁开始
        while (1!=2){
            if(cacheForMin.get(String.valueOf(DateUtils.transformToMinLong(System.currentTimeMillis())))!=null){
                try {
                    //log.info("..................... 自旋锁 沉睡200毫秒.....................");
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                log.info("..................... remove first cacheForMin .....................");
                //printAllCacheForMin(0);
                long now = System.currentTimeMillis();
                now = DateUtils.transformToMinLong(now);
                String firstKey = cacheForMin.entrySet().iterator().next().getKey();
                cacheForMin.put(String.valueOf(now),String.valueOf(now));
                if(DateUtils.getDatePoor(now,Long.parseLong(firstKey))>=5){
                    log.info("..................... 大于5分钟了，拿到第一个元素准备删除 key:{}.....................",firstKey);
                    cacheForMin.remove(firstKey);
                    log.info("..................... remove first cacheForMin end.....................");
                }

                //printAllCacheForMin(1);
            }
        }
    }

    //打印所有元素
    public void printAllCacheForMin(int tag){
        Map<String,String> map = cacheForMin;
        if(map!=null){
            for (Map.Entry<String,String> entry : map.entrySet()) {
                String name = tag==0?"[删除前]":"[删除后]";
                log.info("{} 遍历所有分钟元素：{}",name,entry.toString());
            }
        }
    }

    //删除、读取锁
    //private boolean lock=false;

    //单例模式获取缓存
    public static Map<String,Object>getStringCache(){
        return stringCacheForTest;
    }

    //单例模式获取缓存
    public  Map<String,String>getCacheForMin(){
        return cacheForMin;
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
