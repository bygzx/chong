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

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author eric
 * @date 2019/7/11 10:02
 * bolt处理数据的框架
 * 计算移动均线
 **/
@SuppressWarnings("serial")
@Slf4j
public class MACountingBolt extends BaseRichBolt {
    private OutputCollector collector;
    private RedisService redisService ;
    @Override
    public void prepare(Map var1, TopologyContext var2, OutputCollector outputCollector){
        collector = outputCollector;
        redisService = GetSpringBean.getBean(RedisService.class);
    }
    @Override
    public void execute(Tuple tuple) {
        Map<String,Long> map =(Map<String,Long>)tuple.getValue(0);
        if(map.size()>0){
            Map.Entry<String,Long> entry = map.entrySet().iterator().next();
            if(entry!=null){
                //
                //tableName = RedisKeys.MIN_CLOSE_PRICE+tradeItem.getTradeName();
                //map.put(tableName,sort);
                //countMA_x( TimeStamp, tradeItem, x);
                Set<Object> set = redisService.rangeByScore(entry.getKey(),entry.getValue(),entry.getValue());
            }
        }
        collector.emit(new Values("2"));
    }
    //计算x均线
    private void countMA_x(long TimeStamp,TradeItem tradeItem,int x){

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }



}
