package com.papa.jstorm.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.alibaba.fastjson.JSON;
import com.papa.config.GetSpringBean;
import com.papa.entity.TradeItem;
import com.papa.redis.RedisService;
import com.papa.util.constant.Constants;
import com.papa.util.date.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;

/**
 * @author eric
 * @date 2019/7/11 10:02
 * bolt处理数据的框架
 **/
@SuppressWarnings("serial")
@Slf4j
public class PrintBolt extends BaseBasicBolt{
    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        try {
            long begin = System.currentTimeMillis();
            RedisService redisService = GetSpringBean.getBean(RedisService.class);
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
                    redisService.hmSet(tableName, key, jsonStr);
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
                    redisService.hmSet(tableName, key, jsonStr);
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
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }
}
