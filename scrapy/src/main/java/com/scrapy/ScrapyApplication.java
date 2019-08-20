package com.scrapy;

import com.scrapy.jstorm.topology.TopologyTest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import com.scrapy.config.GetSpringBean;

@SpringBootApplication
@ComponentScan(basePackages={"com.scrapy.controller",
		"com.scrapy.service", "com.redis",
		"com.scrapy.config", "com.scrapy.jstorm.topology",
		"com.scrapy.cache", "com.scrapy.main"})
public class ScrapyApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ScrapyApplication.class, args);
		GetSpringBean springBean=new GetSpringBean();
		springBean.setApplicationContext(context);
		/*TopologyTest app = context.getBean(TopologyTest.class);
		app.main(args);*/
	}

}
