package com.scrapy.service;

import com.alibaba.fastjson.JSONObject;
import com.exception.HttpRequestException;

/**
 * @author eric
 * @date 2019/7/9 12:33
 **/
public interface FXService {
    JSONObject scan(String page,String vtype) ;
    JSONObject test() ;
    JSONObject getDataByName(String name);
    void testCache();
    void deleteFristCache();
    JSONObject swichData(String name);

    JSONObject getFx678Sid();
}
