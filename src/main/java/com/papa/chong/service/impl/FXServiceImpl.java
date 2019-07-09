package com.papa.chong.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.papa.chong.service.FXService;
import com.papa.exception.HttpRequestException;
import com.papa.util.http.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author eric
 * @date 2019/7/9 12:34
 **/
@Service
@Slf4j
public class FXServiceImpl implements FXService {
    @Override
    public JSONObject scan(String page,String vtype)  {
        String url = "https://dataapi.2rich.net/quote/handler/Datas.ashx";
        Map<String,String> map = new HashMap<>();
        map.put("page",page);
        map.put("vtype",vtype);
        String respon = null;
        try {
            respon = HttpRequestUtil.doPostAndReturnString(url,map);
        } catch (HttpRequestException e) {
            //e.printStackTrace();
            log.error("请求失败，再请求一遍");
            try {
                respon = HttpRequestUtil.doPostAndReturnString(url,map);
            } catch (HttpRequestException e1) {
                log.error("最终请求失败，拉倒");
            }
        }finally {
            log.info("respon:{}",respon);
            return null;
        }
    }
}
