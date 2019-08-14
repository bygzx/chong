package com.scrapy.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Lucifer
 * @date 2019-07-09 21:02
 **/
@Data
public class TradeItem implements Serializable {
    private static final long serialVersionUID = -6957361951748382519L;
    private String TradeCodeOnLine;
    private String TradeName;
    private String TradePrice;
    private String Change;
    private String ChangePre;
    private String EnglishName;
    private String timeStamp;
    private String openPrice;
    private String closePrice;
    private String highPrice;
    private String lowPrice;
    private Integer count;
    private String ma5;
    private String ma10;
    private String ma15;
    private String ma30;
    private String ma60;
    private String ma120;
    private String ma240;
}
