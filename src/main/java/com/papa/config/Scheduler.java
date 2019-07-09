package com.papa.config;

import com.papa.chong.service.FXService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Lucifer
 * @date 2019-05-18 23:44
 **/
@Slf4j
@Component
public class Scheduler {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    @Autowired
    private FXService fXService;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,10, 30, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(50));
    //每隔5分钟执行一次
    //@Scheduled(fixedRate = 500000)
    //每天早上7点到23点每隔5分钟执行一次
    //google服务器时间段
    //@Scheduled(cron="0 */5 3-19 * * ?")
    //中国服务器时间段1
    @Scheduled(cron="*/5 * * * * ?")
    public void testTasks() {
        //<1> 查看当前的时区
        ZoneId defaultZone = ZoneId.systemDefault();
        log.info("当前时区：{}",defaultZone); //此处打印为时区所在城市Asia/Shanghai
        log.info("定时任务执行时间：" + dateFormat.format(new Date()));
        int isProxy = 0;
        String osName = System.getProperty("os.name"); //操作系统名称
        //log.info("[Scheduler]#################osName####################{}",osName);
        if(osName!=null && osName.contains("Windows")){
            isProxy = 1;
        }
        int finalIsProxy = isProxy;
        threadPoolExecutor.submit(() ->
        {
            try {
                fXService.test();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //每天3：05执行
    /*@Scheduled(cron = "0 05 03 ? * *")
    public void testTasks() {
        System.out.println("定时任务执行时间：" + dateFormat.format(new Date()));
    }*/
}
