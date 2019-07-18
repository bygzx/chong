package com.papa.jstorm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import com.alibaba.fastjson.JSON;
import com.papa.cache.GuavaCacheService;
import com.papa.config.GetSpringBean;
import com.papa.entity.TradeItem;
import com.papa.redis.RedisService;
import com.papa.util.constant.Constants;
import com.papa.util.constant.RedisKeys;
import com.papa.util.date.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.Date;
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
        Map<String,Long> map = new HashMap<>();
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

                String tableName = "k_" + name;
                //log.info("*********************步骤8**************************{}",entry.toString());
                String key = name + "_" + String.valueOf(timeStamp);
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
                    map = putMinClosePriceRedis(timeStamp,tradeItem);
                } else {
                    //log.info("*********************步骤2-1**************************{}",entry.toString());
                    TradeItem tradeItemTemp = entry.getValue();
                    //log.info("*********************步骤2-1**************************{}",entry.toString());
                    String jsonStr = redisService.hmGet(tableName, key).toString();
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
                    //log.info("*********************步骤2-4**************************{}",entry.toString());
                    //生成hashkey
                    redisService.hmSet(tableName, key, jsonStr);
                    //log.info("*********************步骤2-5**************************{}",entry.toString());
                    //生成有序序列
                    map = putMinClosePriceRedis(timeStamp,tradeItem);
                }
                //String mesg = tuple.getString(0);
                if (entry != null) {
                    long endDate = System.currentTimeMillis();
                    //500毫秒的再打印
                    //if((endDate- begin)>500) {
                        log.info("处理完毕，处理时长：{},数据：{}", endDate - begin, entry.toString());
                    //}
                }
            }else{
                //log.info("没数据可以处理了！！！！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            collector.emit(new Values(map));
        }
    }

    private Map<String,Long> putMinClosePriceRedis(long timeStamp,TradeItem tradeItem){
        //log.info("*********************步骤3-1**************************{}",tradeItem.toString());
        Map<String,Long> map = new HashMap<>();
        String tableName = "";
        long sort = DateUtils.transformToMinLong(timeStamp);
        /*Date date = new Date(timeStamp);
        //log.info("*********************步骤3-2**************************{}",tradeItem.toString());
        String dateStr = DateUtils.format(date, Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM);
        //log.info("*********************步骤3-3**************************{}",tradeItem.toString());
        try {
            Date date1 = DateUtils.parseDate(dateStr,"yyyy-MM-dd HH:mm");
            //log.info("*********************步骤3-4**************************{}",tradeItem.toString());
            sort = date1.getTime();*/
            tableName = RedisKeys.MIN_CLOSE_PRICE.getName()+tradeItem.getTradeName();
            //log.info("*********************步骤3-5**************************{}",tradeItem.toString());
            map.put(tableName,sort);
            //log.info("*********************步骤3-6**************************{}",tradeItem.toString());
            //String lockName = tableName+"_"+sort;
            //log.info("*********************步骤3-7**************************{}",tradeItem.toString());
            guavaCacheService.put(2,tableName+"_"+sort,tradeItem.getClosePrice());
            /*if(redisService.lock(lockName)){
                //log.info("*********************步骤4-1**************************{}",tradeItem.toString());
                if(redisService.rangeByScore(tableName,sort,sort)!=null){
                    //log.info("*********************步骤4-2**************************{}",tradeItem.toString());
                    redisService.zDelete(tableName,sort,sort);
                }
                //log.info("*********************步骤4-3**************************{}",tradeItem.toString());
                redisService.zAdd(tableName,tradeItem.getClosePrice()+"_"+sort,sort);
                //log.info("*********************步骤4-4**************************{}",tradeItem.toString());
                redisService.releaseLock(lockName);
            }else{
                //log.info("*********************步骤5-1**************************{}",tradeItem.toString());
                try {
                    Thread.sleep(1000);
                    //log.info("*********************步骤5-2**************************{}",tradeItem.toString());
                    if(redisService.lock(lockName)){
                        //log.info("*********************步骤5-3**************************{}",tradeItem.toString());
                        if(redisService.rangeByScore(tableName,sort,sort)!=null){
                            //log.info("*********************步骤5-4**************************{}",tradeItem.toString());
                            redisService.zDelete(tableName,sort,sort);
                            //log.info("*********************步骤5-5**************************{}",tradeItem.toString());
                        }
                        redisService.zAdd(tableName,tradeItem.getClosePrice()+"_"+sort,sort);
                        //log.info("*********************步骤5-6**************************{}",tradeItem.toString());
                        redisService.releaseLock(lockName);
                        //log.info("*********************步骤5-7**************************{}",tradeItem.toString());
                    }else{
                        log.info("获取锁失败，此条数据不处理");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
            //log.info("*********************步骤3-8**************************{}",tradeItem.toString());
        return map;
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }
}
