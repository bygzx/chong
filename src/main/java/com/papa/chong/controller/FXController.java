package com.papa.chong.controller;

import com.alibaba.fastjson.JSONObject;
import com.papa.chong.service.ChartService;
import com.papa.chong.service.FXService;
import com.papa.exception.HttpRequestException;
import com.papa.util.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author eric
 * @date 2019/7/9 11:33
 **/
@RestController
@RequestMapping("/fx")
public class FXController extends AbstractController {

    @Autowired
    private FXService fxService;
    @Autowired
    private ChartService chartService;

    @GetMapping("/hello")
    public Object hello(){
        JSONObject jsonObject = new JSONObject();
        Date date = new Date();
        jsonObject.put("date",date);
        jsonObject.put("as","我是嘿嘿嘿");
        jsonObject.put("bs1",null);
        jsonObject.put("bs2","");
        jsonObject.put("success1",false);
        jsonObject.put("success2",true);
        return buildSuccess(jsonObject);
    }

    @PostMapping("/scan")
    public Object scan(String page,String vtype) {
        if(StringUtils.isEmpty(page)){
            page = "fx168-xhwh-mz";
        }
        if(StringUtils.isEmpty(vtype)){
            vtype = "XHWH";
        }
        JSONObject jsonObject = fxService.scan(page,vtype);
        return buildSuccess(jsonObject);
    }

    @PostMapping("/test")
    public Object test() {
        JSONObject jsonObject = fxService.test();
        return buildSuccess(jsonObject);
    }
    @PostMapping("/getDataByName")
    public Object getDataByName(String name) {
        JSONObject jsonObject = fxService.getDataByName(name);
        return buildSuccess(jsonObject);
    }

    @PostMapping("/swichData")
    public Object swichData(String name) {
        JSONObject jsonObject = fxService.swichData(name);
        return buildSuccess(jsonObject);
    }

    @RequestMapping("/getDataToFront")
    public Object getDataToFront(String name) {
        JSONObject jsonObject = chartService.getDataToFront(name);
        return buildSuccess(jsonObject);
    }

    @PostMapping("/getRange")
    public Object getRange(String name, long begin, long end) {
        JSONObject jsonObject = chartService.getRange(name,begin,end);
        return buildSuccess(jsonObject);
    }

    @PostMapping("/deleteFristCache")
    public Object deleteFristCache() {
        fxService.deleteFristCache();
        return buildSuccess(111);
    }

    @PostMapping("/getFx678Sid")
    public Object getFx678Sid() {
        JSONObject jsonObject = fxService.getFx678Sid();
        return buildSuccess(jsonObject);
    }

}
