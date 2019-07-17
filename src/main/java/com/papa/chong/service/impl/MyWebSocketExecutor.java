package com.papa.chong.service.impl;

import com.papa.chong.service.WebSocketExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author eric
 * @date 2019/7/17 8:49
 * 自定义的WebSocket处理器
 * 用于保存或处理WebSocket数据
 **/
@Slf4j
public class MyWebSocketExecutor implements WebSocketExecutor {

    public Queue<String> msgQueue = new LinkedBlockingQueue<>();

    private Boolean isHasClosed = false;

    private int i = 0;

    @Override
    public void doMessage(String msg) {
        //msgQueue.add(msg);
        i++;
        log.info("msgQueue.size:{}",i);
    }

    @Override
    public boolean needClose(String msg) {
        //1分钟14条数据，30分钟就420条，排除掉 sdata开头的数据
        //return msgQueue.size() >= 4200;
        return i >= 4200;
    }

    @Override
    public void setClose(boolean close) {
        isHasClosed = close;
    }

    @Override
    public boolean isClosed() {
        return isHasClosed;
    }
}
