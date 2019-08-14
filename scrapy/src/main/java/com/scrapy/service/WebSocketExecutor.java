package com.scrapy.service;

/**
 * @author eric
 * @date 2019/7/17 8:46
 **/
public interface WebSocketExecutor {
    void doMessage(String msg);
    boolean needClose(String msg);
    void setClose(boolean close);
    boolean isClosed();
}
