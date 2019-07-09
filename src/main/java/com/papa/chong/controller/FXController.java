package com.papa.chong.controller;

import com.alibaba.fastjson.JSONObject;
import com.papa.util.controller.AbstractController;
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
}
