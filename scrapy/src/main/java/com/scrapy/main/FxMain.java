package com.scrapy.main;

import com.scrapy.service.FXService;
import com.scrapy.config.GetSpringBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author eric
 * @date 2019/7/17 14:21
 **/
@Slf4j
@Component("startupRunner")
public class FxMain implements CommandLineRunner{
    @Resource
    private FXService fxService;

    @Override
    public void run(String... args) throws Exception {
        log.info("FxMain init");
        //fxService = GetSpringBean.getBean(FXService.class);
        fxService.getFx678Sid();
        log.info("FxMain init end");
    }
}
