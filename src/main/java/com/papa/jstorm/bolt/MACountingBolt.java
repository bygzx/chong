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
import org.springframework.util.StringUtils;

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
        Map.Entry<String,Long> entry = null;
        if(map.size()>0){
            entry = map.entrySet().iterator().next();
            if(entry!=null){
                //获取时间序列
                long dateLong = entry.getValue();
                String tableName = entry.getKey();
                countMA_x(dateLong,tableName,5);
                countMA_x(dateLong,tableName,10);
                countMA_x(dateLong,tableName,15);
                countMA_x(dateLong,tableName,30);
                countMA_x(dateLong,tableName,60);
                countMA_x(dateLong,tableName,120);
                countMA_x(dateLong,tableName,240);
            }
        }
        collector.emit(new Values(entry));
    }
    //计算x均线
    private void countMA_x(long dateLong,String tableName,int x){
        long beinDateLong = DateUtils.addMin(x,dateLong);
        //TODO 存在逻辑问题，周末数据会获取错误
        Set<Object> set = redisService.rangeByScore(tableName,beinDateLong,dateLong);
        String maX = "";
        if(set!=null && set.size()>0 &&set.size()>=x){
            double d = 0;
            for(Object o:set){
                d = d+Double.parseDouble(o.toString().split("_")[0]);
            }
            maX = String.valueOf((d/set.size()));
        }
        //存到redis
        if(!StringUtils.isEmpty(maX)) {
            String tableName1 = tableName.replace(RedisKeys.MIN_CLOSE_PRICE + "", "") + RedisKeys.MA + x;
            redisService.zAdd(tableName1, maX, dateLong);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }



}
