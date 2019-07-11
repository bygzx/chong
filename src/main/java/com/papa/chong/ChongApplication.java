package com.papa.chong;

import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.AuthorizationException;
import backtype.storm.generated.InvalidTopologyException;
import com.papa.config.GetSpringBean;
import com.papa.jstorm.topology.TopologyTest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"com.papa.chong.controller",
		"com.papa.chong.service","com.papa.redis",
		"com.papa.config","com.papa.jstorm.topology",
		"com.papa.cache"})
public class ChongApplication {

	/**
	 * 非工程启动入口，所以不用main方法
	 * 加上synchronized的作用是由于storm在启动多个bolt线程实例时，如果Springboot用到Apollo分布式配置，会报ConcurrentModificationException错误
	 * 详见：https://github.com/ctripcorp/apollo/issues/1658
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ChongApplication.class, args);
		GetSpringBean springBean=new GetSpringBean();
		springBean.setApplicationContext(context);
		TopologyTest app = context.getBean(TopologyTest.class);
		app.main(args);

	}

}
