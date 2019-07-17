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

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
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
    private GuavaCacheService guavaCacheService;
    @Override
    public void prepare(Map var1, TopologyContext var2, OutputCollector outputCollector){
        collector = outputCollector;
        redisService = GetSpringBean.getBean(RedisService.class);
        guavaCacheService = GetSpringBean.getBean(GuavaCacheService.class);
    }
    @Override
    public void execute(Tuple tuple) {

        Map<String,Long> map =(Map<String,Long>)tuple.getValue(0);
        Map.Entry<String,Long> entry = null;
        if(map.size()>0){
            log.info("MACountingBolt接到数据，开始处理");
            entry = map.entrySet().iterator().next();
            if(entry!=null) {
                //获取当前分钟的long值
                Date date = new Date();
                String dateStr = DateUtils.format(date, Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM);
                Date date1 = null;
                try {
                    date1 = DateUtils.parseDate(dateStr, "yyyy-MM-dd HH:mm");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long nowMin = date1.getTime();
                //long beinDateLong = DateUtils.addMin(-1,nowMin);
                Map.Entry<String, Object> entry1 = (Map.Entry<String, Object>) guavaCacheService.getFirstCacheValue(2);
                if(entry1!=null && !StringUtils.isEmpty(entry1.getKey())){
                    String keyName = entry1.getKey().split("_")[0];
                    long dateLong1 = Long.parseLong(entry1.getKey().split("_")[1]);
                    int mindiff = DateUtils.getDatePoor(nowMin, dateLong1);
                    if (mindiff > 0) {
                        for (int i = 1; i <= mindiff; i++) {
                            Date newDate = DateUtils.addMin(-i, date);
                            String dateStr1 = DateUtils.format(newDate, Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM);
                            try {
                                newDate = DateUtils.parseDate(dateStr1, "yyyy-MM-dd HH:mm");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long newDateLong = newDate.getTime();
                            keyName = keyName + "_" + newDateLong;
                            guavaCacheService.printAllCacheValue(2);
                            if (guavaCacheService.getCacheByKey(2, keyName) != null) {
                                redisService.zAdd(entry1.getKey().split("_")[0], entry1.getValue() + "_" + newDateLong, newDateLong);
                                guavaCacheService.deleteItemByKey(2, keyName);
                            }
                        }
                    }
                }
                /*Map<String,Object> map1 = guavaCacheService.getStringCache(2);
                if(map1!=null && map1.size()>0){
                    Iterator<String> iterator = map1.keySet().iterator();
                    while(iterator.hasNext()) {
                        String  key = iterator.next();
                        String name = key.split("_")[0];
                        long date2 = Long.parseLong(key.split("_")[1]);
                        //小于当前分钟的数据都丢到redis
                        if(date2<nowMin){
                            redisService.zAdd(name,map1.get(key)+"_"+date2,date2);
                            //删除这个值
                            iterator.remove();
                            //guavaCacheService.deleteItemByKey(2,key);
                        }
                    }
                }*/
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
        long beinDateLong = DateUtils.addMin(-x,dateLong);
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
            String tableName1 = tableName.replace(RedisKeys.MIN_CLOSE_PRICE.getName() + "", "") + RedisKeys.MA.getName() + x;
            redisService.zAdd(tableName1, maX, dateLong);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }



}
