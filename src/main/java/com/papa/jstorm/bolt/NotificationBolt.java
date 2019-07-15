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
 * @date 2019/7/12 11:03
 * 根据点阵预警
 **/
@SuppressWarnings("serial")
@Slf4j
public class NotificationBolt extends BaseRichBolt {
    private OutputCollector collector;
    private RedisService redisService ;
    @Override
    public void prepare(Map var1, TopologyContext var2, OutputCollector outputCollector){
        collector = outputCollector;
        redisService = GetSpringBean.getBean(RedisService.class);
    }
    @Override
    public void execute(Tuple tuple) {
        if(tuple!=null&& tuple.getValue(0)!=null){
            Map.Entry<String,Long> entry =(Map.Entry<String,Long>)tuple.getValue(0);
            //TODO 根据各种策略取数对比判断是否需要发消息

        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }
}
