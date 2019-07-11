package com.papa.jstorm.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import com.papa.jstorm.bolt.PrintBolt;
import com.papa.jstorm.spout.RandomSentenceSpout;
import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author eric
 * @date 2019/7/11 10:06
 **/
@Component
public class TopologyTest {


    public static  void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();
        Config config = new Config();

        builder.setSpout("RandomSentence", new RandomSentenceSpout(), 2);
        builder.setBolt("WordNormalizer", new PrintBolt(), 2).shuffleGrouping("RandomSentence");
        config.setDebug(false);
        /*// 配置zookeeper连接主机地址，可以使用集合存放多个
        config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList("127.0.0.1"));
        // 配置zookeeper连接端口，默认2181
        config.put(Config.STORM_ZOOKEEPER_PORT, 2181); */
        /*
         * 初级工程师本地模式和准生产测试时，topology的work的数量都为1，
         * 导致对象在bolt和bolt节点传输时并没有走序列化方式，结果测试一切正常， 但是上生产后，因为work数量是10个，
         * 立马在后一个bolt中报大量的空指针异常， 造成很严重的生产问题。
         */
        //config.setMaxTaskParallelism(1);
        config.setNumWorkers(1);
        //本地模式
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("wordcount", config, builder.createTopology());
    }
}
