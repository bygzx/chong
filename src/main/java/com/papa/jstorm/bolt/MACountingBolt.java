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
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

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
    private GuavaCacheService guavaCacheService;
    @Override
    public void prepare(Map var1, TopologyContext var2, OutputCollector outputCollector){
        collector = outputCollector;
        redisService = GetSpringBean.getBean(RedisService.class);
        guavaCacheService = GetSpringBean.getBean(GuavaCacheService.class);
    }
    @Override
    public void execute(Tuple tuple) {

        Map<String,String> map =(Map<String,String>)tuple.getValue(0);
        //<tableName,key>
        Map.Entry<String,String> entry = null;
        List<String> minList = new ArrayList();
        int keepMin = 0;
        if(map.size()>0){
            log.info("MACountingBolt接到数据，开始处理");
            entry = map.entrySet().iterator().next();
            if(entry!=null && !"1".equals(entry.getKey())) {
                Object o = redisService.hmGet(entry.getKey(), entry.getValue());
                if(o!=null){
                    String value = String.valueOf(o);
                    //计算小数点后留多少位
                    keepMin = value.substring(value.indexOf(".")+1,value.length()).length();


                    Map<String,String> minMapList = guavaCacheService.getCacheForMin();
                    long date = DateUtils.addMin(-239,Long.parseLong(entry.getKey()));
                    date = DateUtils.transformToMinLong(date);
                    for(Map.Entry<String,String> entry1:minMapList.entrySet()){
                        if(Long.parseLong(entry1.getKey())>=date &&!entry1.getKey().equals(entry.getValue())){
                            minList.add(entry1.getKey());
                        }
                        if(entry1.getKey().equals(entry.getValue())){
                            minList.add(entry1.getKey());
                            break;
                        }else if(Long.parseLong(entry1.getKey())>Long.parseLong(entry.getValue())){
                            break;
                        }
                    }
                }
                countMA_x(entry.getKey(),entry.getValue(),5,minList,keepMin);
                countMA_x(entry.getKey(),entry.getValue(),10,minList,keepMin);
                countMA_x(entry.getKey(),entry.getValue(),15,minList,keepMin);
                countMA_x(entry.getKey(),entry.getValue(),30,minList,keepMin);
                countMA_x(entry.getKey(),entry.getValue(),60,minList,keepMin);
                countMA_x(entry.getKey(),entry.getValue(),120,minList,keepMin);
                countMA_x(entry.getKey(),entry.getValue(),240,minList,keepMin);
            }
        }
        collector.emit(new Values(entry));
    }
    //计算x均线

    /**
     *
     * @param tableName
     * @param key
     * @param x
     * @param minList
     * @param keepNo 保留小数点后多少位
     */
    private void countMA_x(String tableName,String key,int x,List<String> minList,int keepNo){
        double d = 0;
        //TODO 存在逻辑问题，周末数据会获取错误
        if(minList.size()>=x){
            for(int i = minList.size();i>minList.size()-x;i--){
                d = d+Double.parseDouble(minList.get(i));
            }
            BigDecimal b = new BigDecimal(d);
            d = b.setScale(keepNo,BigDecimal.ROUND_HALF_UP).doubleValue();
            String jsonStr = redisService.hmGet(tableName, key).toString();
            TradeItem tradeItem = JSON.parseObject(jsonStr, TradeItem.class);
            switch (x){
                case 5:tradeItem.setMa5(String.valueOf(d)); break;
                case 10:tradeItem.setMa10(String.valueOf(d));break;
                case 15:tradeItem.setMa15(String.valueOf(d));break;
                case 30:tradeItem.setMa30(String.valueOf(d));break;
                case 60:tradeItem.setMa60(String.valueOf(d));break;
                case 120:tradeItem.setMa120(String.valueOf(d));break;
                case 240:tradeItem.setMa240(String.valueOf(d));break;
                default:break;
            }
            jsonStr = JSON.toJSONString(tradeItem);
            redisService.hmSet(tableName, key,jsonStr);

        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }
}
