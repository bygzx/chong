package com.papa.websocket;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.papa.cache.GuavaCacheService;
import com.papa.chong.service.FXService;
import com.papa.chong.service.WebSocketExecutor;
import com.papa.config.GetSpringBean;
import com.papa.dto.Fx678Dto;
import com.papa.entity.TradeItem;
import com.papa.util.constant.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author eric
 * @date 2018/7/23 15:58
 * 客户端
 **/
@Slf4j
public class MyWebSocketClient extends WebSocketClient {

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,10, 30, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(50));

    /**
     * 1-心跳 2-获取数据
     */
    private int tag;
    private WebSocketExecutor executor; // 用于保存获取的数据、判定是否需要关闭连接的功能类
    private GuavaCacheService guavaCacheService;
    private FXService fxService;
    public MyWebSocketClient(String url, WebSocketExecutor executor,int tag) throws URISyntaxException {
        super(new URI(url));
        this.executor = executor;
        this.tag = tag;
        guavaCacheService = GetSpringBean.getBean(GuavaCacheService.class);
        fxService = GetSpringBean.getBean(FXService.class);
    }

    @Override
    public void onOpen(ServerHandshake shake) {
        log.info("TAG:{},握手...",tag);
        for(Iterator<String> it=shake.iterateHttpFields();it.hasNext();) {
            String key = it.next();
            log.info(key+":"+shake.getFieldValue(key));
        }
    }

    @Override
    public void onMessage(String paramString) {
        if(paramString.length()<10) {
            log.info("TAG:{},接收到心跳数据消息：{}", tag, paramString);
        }/*else if(paramString.indexOf("EURUSD")>=0){
            log.info("TAG:{},接收到需要处理的数据消息：{}", tag, paramString);
        }*/

        if(paramString.equals("3probe")&&tag==1){
            send("5");
            sentHeartBeat();
        }else if(paramString.equals("3")){
            sentHeartBeat();
        }else if(paramString.equals("3probe")&&tag==2) {
            send("5");
            sentHeartBeat();
        }else if(paramString.indexOf("data")>=0 && paramString.indexOf("sdata")<0){
            paramString = paramString.substring(2,paramString.length());
            paramString = paramString.replace("[","");
            paramString = paramString.replace("]","");
            paramString = paramString.replace("\"data\",","");
            Fx678Dto fx678Dto = JSON.parseObject(paramString,Fx678Dto.class);
                if(fx678Dto.getI().equals("USD")||fx678Dto.getI().equals("XAU")||fx678Dto.getI().equals("EURUSD")||
                        fx678Dto.getI().equals("GBPUSD")){
                    //log.info("TAG:{},接收到需要处理的数据消息：{}", tag, paramString);
                    executor.doMessage(paramString);
                    String name = RedisKeys.USDINDEX.toString().replace(".", "");
                    switch (fx678Dto.getI()){
                        case "EURUSD": {
                            name = RedisKeys.EURUSD.toString().replace(".", "");break;
                        }
                        case "XAU":
                            name = RedisKeys.GOLD.toString().replace(".", "");break;
                        case "USD":
                            name = RedisKeys.USDINDEX.toString().replace(".", "");break;
                        case "GBPUSD":
                            name = RedisKeys.GBPUSD.toString().replace(".", "");break;
                    }
                    TradeItem tradeItem = new TradeItem();
                    tradeItem.setClosePrice(fx678Dto.getC());
                    tradeItem.setTradePrice(fx678Dto.getC());
                    tradeItem.setLowPrice(fx678Dto.getL());
                    tradeItem.setHighPrice(fx678Dto.getH());
                    tradeItem.setOpenPrice(fx678Dto.getO());
                    tradeItem.setTradeName(name);
                    tradeItem.setTimeStamp(fx678Dto.getT());
                    pushToCache(Long.parseLong(fx678Dto.getT()),tradeItem);
                }
        }
        if(executor.needClose(paramString)){
            this.close();
            executor.setClose(true);
        }
    }

    private void sentHeartBeat(){
        threadPoolExecutor.submit(() -> {
            try {
                log.info("TAG:{},*******异步线程发送心跳2******", tag);
                Thread.sleep(25 * 1000);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            send("2");
        });
    }

    //把数据丢到本地缓存给jstorm处理
    private void pushToCache(long timeStemp,TradeItem tradeItem){
        String name = tradeItem.getTradeName()+"_"+timeStemp;
        guavaCacheService.put(name,tradeItem);
    }

    @Override
    public void onClose(int paramInt, String paramString, boolean paramBoolean) {
        if (!executor.isClosed()) {
            executor.setClose(true);
        }
        log.info("TAG:{},关闭...",tag);
    }

    @Override
    public void onError(Exception e) {
        log.info("异常"+e);
    }
}
