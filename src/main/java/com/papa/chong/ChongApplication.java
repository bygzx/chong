package com.papa.chong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"com.papa.chong.controller","com.papa.chong.service"})
public class ChongApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChongApplication.class, args);
	}

}
