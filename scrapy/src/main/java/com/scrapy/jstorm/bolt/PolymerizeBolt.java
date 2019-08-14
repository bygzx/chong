package com.scrapy.jstorm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.alibaba.fastjson.JSON;
import com.scrapy.cache.GuavaCacheService;
import com.scrapy.config.GetSpringBean;
import com.scrapy.entity.TradeItem;
import com.redis.RedisService;
import com.util.constant.Constants;
import com.util.constant.RedisKeys;
import com.util.date.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author eric
 * @date 2019/7/12 11:04
 * bolt处理数据的框架
 * 聚合日K
 **/
@SuppressWarnings("serial")
@Slf4j
public class PolymerizeBolt extends BaseRichBolt {
    private OutputCollector collector;
    private RedisService redisService ;
    private GuavaCacheService guavaCacheService;
    @Override
    public void prepare(Map var1, TopologyContext var2, OutputCollector var3){
        collector = var3;
        redisService = GetSpringBean.getBean(RedisService.class);
        guavaCacheService = GetSpringBean.getBean(GuavaCacheService.class);
    }

    @Override
    public void execute(Tuple tuple) {
        // 存放 <tableName,key>
        Map<String,String> map = new HashMap<>();
        String tableName = "1";
        String key =  "1";
        try {
            String closeKey = "";
            long begin = System.currentTimeMillis();
            if(tuple!=null &&tuple.getValue(0)!=null) {
                //log.info("*********************步骤1**************************{}");
                Map.Entry<String, TradeItem> entry = (Map.Entry<String, TradeItem>) tuple.getValue(0);
                //log.info("*********************步骤2**************************{}",entry.toString());
                String name = entry.getKey().split("_")[0];
                //log.info("*********************步骤3**************************{}",entry.toString());
                long timeStamp = DateUtils.transformToMinLong(Long.parseLong(entry.getValue().getTimeStamp()+"000"));

                tableName = "k_" + name;
                //log.info("*********************步骤8**************************{}",entry.toString());
                //String key = name + "_" + String.valueOf(timeStamp);
                key =  String.valueOf(timeStamp);
                //log.info("*********************步骤9**************************{}",entry.toString());
                if (redisService.hmGet(tableName, key) == null) {
                    //log.info("*********************步骤1-1**************************{}",entry.toString());
                    TradeItem tradeItem = entry.getValue();
                    //log.info("*********************步骤1-2**************************{}",entry.toString());
                    tradeItem.setTradeName(name);
                    tradeItem.setClosePrice(tradeItem.getTradePrice());
                    tradeItem.setOpenPrice(tradeItem.getTradePrice());
                    tradeItem.setHighPrice(tradeItem.getTradePrice());
                    tradeItem.setLowPrice(tradeItem.getTradePrice());
                    tradeItem.setTimeStamp(tradeItem.getTimeStamp());
                    tradeItem.setCount(1);
                    //log.info("*********************步骤1-3**************************{}",entry.toString());
                    String jsonStr = JSON.toJSONString(tradeItem);
                    //log.info("*********************步骤1-4**************************{}",entry.toString());
                    //生成hashkey
                    redisService.hmSet(tableName, key, jsonStr);
                    //log.info("*********************步骤1-5**************************{}",entry.toString());
                    //生成点阵序列
                    //map = putMinClosePriceRedis(timeStamp,tradeItem);
                } else {
                    //log.info("*********************步骤2-1**************************{}",entry.toString());
                    TradeItem tradeItemTemp = entry.getValue();
                    //log.info("*********************步骤2-1**************************{}",entry.toString());
                    String jsonStr = "";
                    Object o = redisService.hmGet(tableName, key);
                    if (o != null) {
                        jsonStr = redisService.hmGet(tableName, key).toString();
                        //log.info("*********************步骤2-2**************************{}",entry.toString());
                        TradeItem tradeItem = JSON.parseObject(jsonStr, TradeItem.class);
                        //log.info("*********************步骤2-3**************************{}",entry.toString());
                        tradeItem.setTimeStamp(tradeItemTemp.getTimeStamp());
                        //更新当前价
                        tradeItem.setTradePrice(tradeItemTemp.getTradePrice());
                        if (Float.parseFloat(tradeItemTemp.getTradePrice()) > Float.parseFloat(tradeItem.getHighPrice())) {
                            tradeItem.setHighPrice(tradeItemTemp.getTradePrice());
                        } else if (Float.parseFloat(tradeItemTemp.getTradePrice()) < Float.parseFloat(tradeItem.getLowPrice())) {
                            tradeItem.setLowPrice(tradeItemTemp.getTradePrice());
                        }
                        tradeItem.setClosePrice(tradeItemTemp.getTradePrice());
                        tradeItem.setCount((tradeItem.getCount() + 1));
                        jsonStr = JSON.toJSONString(tradeItem);
                        redisService.hmSet(tableName, key, jsonStr);
                    }
                    else{
                        log.error("[PolymerizeBolt] 获取数据失败 key:{},value:{}", tableName, key);
                        //TODO 数据没拿到的情况
                        tableName = "1";
                        key =  "1";
                    }
                    if (entry != null) {
                        long endDate = System.currentTimeMillis();
                        log.info("处理完毕，处理时长：{},数据：{}", endDate - begin, entry.toString());
                    }
                }
            }else{
                //log.info("没数据可以处理了！！！！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            map.put(tableName,key);
            collector.emit(new Values(map));
        }
    }

    /*private Map<String,Long> putMinClosePriceRedis(long timeStamp,TradeItem tradeItem){
        Map<String,Long> map = new HashMap<>();
        String tableName = "";
        long sort = DateUtils.transformToMinLong(timeStamp);
            tableName = RedisKeys.MIN_CLOSE_PRICE.getName()+tradeItem.getTradeName();
            guavaCacheService.put(2,tableName+"_"+sort,tradeItem.getClosePrice());
        return map;
    }*/


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }
}
