package com.papa.entity;

import lombok.Data;

/**
 * @author Lucifer
 * @date 2019-07-09 21:02
 **/
@Data
public class TradeItem {
    private String TradeCodeOnLine;
    private String TradeName;
    private String TradePrice;
    private String Change;
    private String ChangePre;
    private String EnglishName;
    private String timeStamp;
}
