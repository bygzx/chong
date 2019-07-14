package com.papa.jstorm.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import com.papa.cache.GuavaCacheService;
import com.papa.config.GetSpringBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Random;

/**
 * @author eric
 * @date 2019/7/11 9:51
 * spout获取数据源
 **/
@SuppressWarnings("serial")
@Slf4j
public class PolymerizeSpout extends BaseRichSpout {

    SpoutOutputCollector spoutOutputCollector;
    //Random random;
    /*@Resource
    private GuavaCacheService guavaCacheService;*/

    // 进行spout的一些初始化工作，包括参数传递
    @SuppressWarnings("rawtypes")
    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector collector) {
        spoutOutputCollector = collector;
        //random = new Random();
    }
    // 进行Tuple处理的主要方法
    @Override
    public void nextTuple() {

        GuavaCacheService guavaCacheService = GetSpringBean.getBean(GuavaCacheService.class);
        Object o = guavaCacheService.getFirstCacheValue();
        if(o!=null) {
            //Map.Entry<String, Object> entry = (Map.Entry<String, Object>) o;
            //拿完就删
            guavaCacheService.deleteFristCacheValue();
            spoutOutputCollector.emit(new Values(o));
        }else{
            spoutOutputCollector.emit(new Values(o));
        }

    }
    // 消息保证机制中的ack确认方法
    //继承IRichSpout则需要自己写
    @Override
    public void ack(Object id) {
    }

    // 消息保证机制中的fail确认方法
    //继承IRichSpout则需要自己写
    @Override
    public void fail(Object id) {
    }

    // 声明字段
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }
}
