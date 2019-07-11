package com.papa.jstorm.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Random;

/**
 * @author eric
 * @date 2019/7/11 9:51
 * spout获取数据源
 **/
@SuppressWarnings("serial")
@Slf4j
public class RandomSentenceSpout extends BaseRichSpout {

    SpoutOutputCollector spoutOutputCollector;
    Random random;

    // 进行spout的一些初始化工作，包括参数传递
    @SuppressWarnings("rawtypes")
    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector collector) {
        spoutOutputCollector = collector;
        random = new Random();
    }
    // 进行Tuple处理的主要方法
    @Override
    public void nextTuple() {
        Utils.sleep(2000);
        String[] sentences = new String[]{
                "jikexueyuan is a good school",
                "And if the golden sun",
                "four score and seven years ago",
                "storm hadoop spark hbase",
                "blogchong is a good man",
                "Would make my whole world bright",
                "blogchong is a good website",
                "storm would have to be with you",
                "Pipe to subprocess seems to be broken No output read",
                " You make me feel so happy",
                "For the moon never beams without bringing me dreams Of the beautiful Annalbel Lee",
                "Who love jikexueyuan and blogchong",
                "blogchong.com is Magic sites",
                "Ko blogchong swayed my leaves and flowers in the sun",
                "You love blogchong.com", "Now I may wither into the truth",
                "That the wind came out of the cloud",
                "at backtype storm utils ShellProcess",
                "Of those who were older than we"};
        // 从sentences数组中，随机获取一条语句，作为这次spout发送的消息
        String sentence = sentences[random.nextInt(sentences.length)];
        log.info("选择的数据源是: {}" , sentence);
        // 使用emit方法进行Tuple发布，参数用Values申明
        spoutOutputCollector.emit(new Values(sentence.trim().toLowerCase()));
    }
    // 消息保证机制中的ack确认方法
    //继承IRichSpout则需要自己写
    /*@Override
    public void ack(Object id) {
    }*/

    // 消息保证机制中的fail确认方法
    //继承IRichSpout则需要自己写
    /*@Override
    public void fail(Object id) {
    }*/

    // 声明字段
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }
}
