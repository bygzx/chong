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
    @Override
    public void prepare(Map var1, TopologyContext var2, OutputCollector var3){
        collector = var3;
        redisService = GetSpringBean.getBean(RedisService.class);
    }

    @Override
    public void execute(Tuple tuple) {
        Map<String,Long> map = new HashMap<>();
        try {
            String closeKey = "";
            long begin = System.currentTimeMillis();

            if(tuple!=null &&tuple.getValue(0)!=null) {
                Map.Entry<String, TradeItem> entry = (Map.Entry<String, TradeItem>) tuple.getValue(0);
                String name = entry.getKey().split("_")[0];
                long timeStamp = Long.parseLong(entry.getKey().split("_")[1]);
                Date date = new Date(timeStamp);
                String dateStr = DateUtils.format(date, Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM);
                String tableName = "k_" + name;
                String key = name + "_" + dateStr;
                if (redisService.hmGet(tableName, key) == null) {
                    TradeItem tradeItem = (TradeItem) entry.getValue();
                    tradeItem.setTradeName(name);
                    tradeItem.setClosePrice(tradeItem.getTradePrice());
                    tradeItem.setOpenPrice(tradeItem.getTradePrice());
                    tradeItem.setHighPrice(tradeItem.getTradePrice());
                    tradeItem.setLowPrice(tradeItem.getTradePrice());
                    tradeItem.setTimeStamp(tradeItem.getTimeStamp());
                    tradeItem.setCount(1);
                    String jsonStr = JSON.toJSONString(tradeItem);
                    //生成hashkey
                    redisService.hmSet(tableName, key, jsonStr);
                    //生成点阵序列
                    map = putMinClosePriceRedis(timeStamp,tradeItem);
                } else {
                    TradeItem tradeItemTemp = (TradeItem) entry.getValue();
                    String jsonStr = redisService.hmGet(tableName, key).toString();
                    TradeItem tradeItem = JSON.parseObject(jsonStr, TradeItem.class);

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
                    //生成hashkey
                    redisService.hmSet(tableName, key, jsonStr);
                    //生成有序序列
                    map = putMinClosePriceRedis(timeStamp,tradeItem);
                }
                //String mesg = tuple.getString(0);
                if (entry != null) {
                    log.info("处理完毕，处理时长：{},数据：{}", System.currentTimeMillis() - begin, entry.toString());
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
        Map<String,Long> map = new HashMap<>();
        String tableName = "";
        long sort = 0L;
        Date date = new Date(timeStamp);
        String dateStr = DateUtils.format(date, Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM);
        try {
            Date date1 = DateUtils.parseDate(dateStr,"yyyy-MM-dd HH:mm");
            sort = date1.getTime();
            tableName = RedisKeys.MIN_CLOSE_PRICE+tradeItem.getTradeName();
            map.put(tableName,sort);
            redisService.zAdd(tableName,tradeItem.getClosePrice()+"_"+sort,sort);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return map;
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }
}
