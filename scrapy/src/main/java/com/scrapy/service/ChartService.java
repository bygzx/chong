package com.scrapy.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author eric
 * @date 2019/7/10 11:03
 **/
public interface ChartService {
    JSONObject getDataToFront(String name);

    JSONObject getRange(String name,long begin,long end);
}
