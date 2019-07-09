package com.papa.chong.controller;

import com.alibaba.fastjson.JSONObject;
import com.papa.chong.service.FXService;
import com.papa.exception.HttpRequestException;
import com.papa.util.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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

    @PostMapping("/hello")
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
}
