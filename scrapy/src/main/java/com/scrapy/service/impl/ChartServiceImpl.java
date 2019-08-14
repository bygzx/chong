package com.scrapy.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.scrapy.service.ChartService;
import com.scrapy.entity.TradeItem;
import com.redis.RedisService;
import com.util.constant.Constants;
import com.util.date.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

/**
 * @author eric
 * @date 2019/7/10 11:03
 **/
@Service
@Slf4j
public class ChartServiceImpl implements ChartService{
    @Resource
    private RedisService redisService;

    @Override
    public JSONObject getDataToFront(String name) {
        JSONObject jsonObject = new JSONObject();
        Map<Object,Object> objects1 = redisService.hmGetAll(name);
        Map<String,String> objects2 = new HashMap<>();
        Map<String,String> objects = new HashMap<>();
        if(objects1!=null){
            for (Map.Entry<Object,Object> entry : objects1.entrySet()) {
                objects2.put(String.valueOf(entry.getKey()),String.valueOf(entry.getValue()));
            }
            objects = sortMap(objects2);
        }
        Map<String,TradeItem> tradeItemMap = new HashMap<>();
        if(objects!=null){

            int i =0;
            for (Map.Entry<String,String> entry : objects.entrySet()) {
                /*i++;
                if(i<100){
                    continue;
                }
                if(i>=300){
                    break;
                }*/

                //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                Date date = new Date(Long.parseLong(String.valueOf(entry.getKey())));
                String dateStr = DateUtils.format(date, Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM);
                //dateStr = dateStr.substring(0,dateStr.length()-3);
                //log.info("转换日期：{}--{}---{}",entry.getKey(), DateUtils.format(date, Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS),dateStr);
                if(!tradeItemMap.containsKey(dateStr)){
                    TradeItem tradeItem = new TradeItem();
                    tradeItem.setTradeName(name);
                    tradeItem.setTradePrice(entry.getValue());
                    tradeItem.setClosePrice(entry.getValue());
                    tradeItem.setOpenPrice(entry.getValue());
                    tradeItem.setHighPrice(entry.getValue());
                    tradeItem.setLowPrice(entry.getValue());
                    tradeItem.setTimeStamp(entry.getKey());
                    tradeItem.setCount(1);
                    tradeItemMap.put(dateStr,tradeItem);
                }else{
                    TradeItem tradeItem = tradeItemMap.get(dateStr);
                    tradeItem.setTimeStamp(String.valueOf(entry.getKey()));
                    //更新当前价
                    tradeItem.setTradePrice(String.valueOf(entry.getValue()));
                    if(Float.parseFloat(entry.getValue())>Float.parseFloat(tradeItem.getHighPrice())){
                        tradeItem.setHighPrice(entry.getValue());
                    }else if(Float.parseFloat(entry.getValue())<Float.parseFloat(tradeItem.getLowPrice())){
                        tradeItem.setLowPrice(entry.getValue());
                    }
                    tradeItem.setClosePrice(entry.getValue());
                    tradeItem.setCount((tradeItem.getCount()+1));
                    tradeItemMap.put(dateStr,tradeItem);
                }

            }
            Map<String,TradeItem> finalMap = sortMapDate(tradeItemMap);
            jsonObject.put("list",finalMap);
            log.info(finalMap.toString());
        }
        return jsonObject;
    }

    @Override
    public JSONObject getRange(String name, long begin, long end) {
        JSONObject jsonObject = new JSONObject();
        Map<Object,Object> objects = redisService.hmGetAll(name);
        Map<String,String> tradeItemMap = new HashMap<>();
        for (Map.Entry<Object,Object> entry : objects.entrySet()) {
            if(Long.parseLong(String.valueOf(entry.getKey()))>=begin &&Long.parseLong(String.valueOf(entry.getKey()))<=end){
                tradeItemMap.put(String.valueOf(entry.getKey()),String.valueOf(entry.getValue()));
            }
        }
        jsonObject.put("list",tradeItemMap);
        log.info("listSize:{}",tradeItemMap.size());
        return jsonObject;
    }

    //按照key的大小排序
    private Map<String, String> sortMap(Map<String, String> k_v) {
        List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(k_v.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                // 升序排序
                return (int)(Long.parseLong(o1.getKey()) - Long.parseLong(o2.getKey()));
            }
        });
        Map result = new LinkedHashMap();
        for (Map.Entry<String, String> entry : list) {
            result.put(entry.getKey(), entry.getValue());

        }
        return result;
    }


    private Map<String, TradeItem> sortMapDate(Map<String, TradeItem> k_v) {
        List<Map.Entry<String, TradeItem>> list = new ArrayList<Map.Entry<String, TradeItem>>(k_v.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, TradeItem>>() {
            @Override
            public int compare(Map.Entry<String, TradeItem> o1,
                               Map.Entry<String, TradeItem> o2) {
                int i = 0;
                // 升序排序
                try {
                    i = (int)(DateUtils.parseDate(o1.getKey(),Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM).getTime() - DateUtils.parseDate(o2.getKey(),Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM).getTime());

                } catch (ParseException e) {
                    e.printStackTrace();
                }finally {
                    return i;
                }
            }
        });
        Map result = new LinkedHashMap();
        for (Map.Entry<String, TradeItem> entry : list) {
            result.put(entry.getKey(), entry.getValue());

        }
        return result;
    }
}
