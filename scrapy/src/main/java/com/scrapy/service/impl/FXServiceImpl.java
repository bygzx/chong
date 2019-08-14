package com.scrapy.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.scrapy.cache.GuavaCacheService;
import com.scrapy.service.FXService;
import com.scrapy.entity.TradeItem;
import com.exception.HttpRequestException;
import com.redis.RedisService;
import com.util.constant.Constants;
import com.util.constant.RedisKeys;
import com.util.date.DateUtils;
import com.util.http.HttpRequestUtil;
import com.scrapy.websocket.MyWebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.URL;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author eric
 * @date 2019/7/9 12:34
 **/
@Service
@Slf4j
public class FXServiceImpl implements FXService {

    @Resource
    private RedisService redisService;
    @Resource
    private GuavaCacheService guavaCacheService;

    private static String realUrl = "https://forex.fx168.com/";

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,10, 30, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(50));

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
            //log.error(e.getMessage());
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

    @Override
    public JSONObject test() {
        //crawlPageWithoutAnalyseJs(0);
        long timeStemp = System.currentTimeMillis();
        String json = getHtml(timeStemp);
        if(!StringUtils.isEmpty(json)){
            json = json.replace("show_whdataHot(","");
            json = json.replace("]})","]}");
            //log.info("json:{}",json);
            JSONObject jsonObject = JSON.parseObject(json);
            //log.info("jsonObject:{}",jsonObject.get("List"));
            List<TradeItem> tradeItems = json2TradeItem(jsonObject.get("List").toString());
            if(tradeItems!=null && tradeItems.size()>0){
                for(TradeItem tradeItem:tradeItems){
                    if(tradeItem.getTradeName().equals("美元指数")||tradeItem.getTradeName().equals("欧元美元")
                            ||tradeItem.getTradeName().equals("英镑美元")||tradeItem.getTradeName().equals("现货黄金")
                            ||tradeItem.getTradeName().equals("美元人民币")) {
                        switch (tradeItem.getTradeName()){
                            case "欧元美元": {
                                tradeItem.setTradeName(RedisKeys.EURUSD.toString().replace(".", ""));
                                redisService.hmSet(RedisKeys.EURUSD.toString().replace(".", ""), String.valueOf(timeStemp), tradeItem.getTradePrice());break;
                            }
                            case "现货黄金":
                                tradeItem.setTradeName(RedisKeys.GOLD.toString().replace(".", ""));
                                redisService.hmSet(RedisKeys.GOLD.toString().replace(".",""), String.valueOf(timeStemp), tradeItem.getTradePrice());break;
                            case "美元人民币":
                                tradeItem.setTradeName(RedisKeys.USDRMB.toString().replace(".", ""));
                                redisService.hmSet(RedisKeys.USDRMB.toString().replace(".",""), String.valueOf(timeStemp), tradeItem.getTradePrice());break;
                            case "美元指数":
                                tradeItem.setTradeName(RedisKeys.USDINDEX.toString().replace(".", ""));
                                redisService.hmSet(RedisKeys.USDINDEX.toString().replace(".",""), String.valueOf(timeStemp), tradeItem.getTradePrice());break;
                            case "英镑美元":
                                tradeItem.setTradeName(RedisKeys.GBPUSD.toString().replace(".", ""));
                                redisService.hmSet(RedisKeys.GBPUSD.toString().replace(".",""), String.valueOf(timeStemp), tradeItem.getTradePrice());break;
                        }
                        //redisService.hmSet(tradeItem.getTradeName(), String.valueOf(timeStemp), tradeItem.getTradePrice());
                        //pushToCache(timeStemp,tradeItem);
                    }
                    //log.info(tradeItem.toString());
                }
            }
        }
        long end = System.currentTimeMillis();
        log.info("获取数据结束-----耗时：{}",end-timeStemp);
        return null;
    }
    //把数据丢到本地缓存给jstorm处理
    private void pushToCache(long timeStemp,TradeItem tradeItem){
        String name = tradeItem.getTradeName()+"_"+timeStemp;
        guavaCacheService.put(name,tradeItem);
    }

    @Override
    public JSONObject getDataByName(String name) {
        JSONObject jsonObject = new JSONObject();
        Map<Object,Object> objects = redisService.hmGetAll(name);
        List<TradeItem> objects1 = new ArrayList<>();
        if(objects!=null){
            int i=0;
            for (Map.Entry<Object,Object> entry : objects.entrySet()) {
                i++;
                if(i>=100){
                    break;
                }else {
                    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                    TradeItem tradeItem = new TradeItem();
                    tradeItem.setTradeName(name);
                    tradeItem.setTradePrice(String.valueOf(entry.getValue()));
                    tradeItem.setTimeStamp(String.valueOf(entry.getKey()));
                    objects1.add(tradeItem);
                }
            }
            jsonObject.put("list",objects1);
        }
        return jsonObject;
    }
    @Override
    public JSONObject swichData(String name) {
        JSONObject jsonObject = new JSONObject();
        Map<Object,Object> objects = redisService.hmGetAll(name);
        Map<Object,Object> Array1 = new HashMap<>();
        Map<Object,Object> Array2 = new HashMap<>();
        Map<Object,Object> Array3 = new HashMap<>();
        Map<Object,Object> Array4 = new HashMap<>();
        Map<Object,Object> Array0 = new HashMap<>();
        List<TradeItem> objects1 = new ArrayList<>();
        if(objects!=null){
            int size = objects.size();
            log.info("总条数：{}",size);
            int i=0;
            for (Map.Entry<Object,Object> entry : objects.entrySet()) {
                i++;
            if(i%5==0){
                Array0.put(entry.getKey(),entry.getValue());
            }else if(i%5==1){
                Array1.put(entry.getKey(),entry.getValue());
            }else if(i%5==2){
                Array2.put(entry.getKey(),entry.getValue());
            }else if(i%5==3){
                Array3.put(entry.getKey(),entry.getValue());
            }else if(i%5==4){
                Array4.put(entry.getKey(),entry.getValue());
            }
            }
            threadPoolExecutor.submit(() ->{submitData(Array0,name,0);});
            threadPoolExecutor.submit(() ->{submitData(Array1,name,1);});
            threadPoolExecutor.submit(() ->{submitData(Array2,name,2);});
            threadPoolExecutor.submit(() ->{submitData(Array3,name,3);});
            threadPoolExecutor.submit(() ->{submitData(Array4,name,4);});
            //
        }
        jsonObject.put("success",Constants.FLAG_1);
        return jsonObject;
    }

    @Override
    public JSONObject getFx678Sid() {
        JSONObject jsonObject = new JSONObject();
        getFx678Data();
        /*long currentTime = System.currentTimeMillis();
        String url = "https://stat.fx678.com:9970/socket.io/?EIO=3&transport=polling&t="+currentTime+"-1";
        String result ="";
        String sid = "";
        try {
            result = HttpRequestUtil.doGetAndReturnString(url);
            sid = getSid(result);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if(!StringUtils.isEmpty(sid)){
            String finalSid = sid;
            log.info("TAG:{},webscoket sid :{}",1,result);
            threadPoolExecutor.submit(() ->{
                int count = 1;
                        MyWebSocketExecutor executor = new MyWebSocketExecutor();
                        try {
                            String wsUrl = "wss://stat.fx678.com:9970/socket.io/?EIO=3&transport=websocket&sid="+ finalSid;
                            log.info("TAG:{},webscoket wsUrl :{}",1,wsUrl);
                            MyWebSocketClient client = new MyWebSocketClient(wsUrl, executor,1);
                            client.connect();
                            while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
                                log.info("TAG:{},还没有打开,client状态：{}",1,client.getReadyState());
                                Thread.sleep( 3 * 1000);
                            }
                            log.info("TAG:{},建立websocket连接",1);

                            client.send("2probe");
                            if(count==1){
                                getFx678Data();
                            }
                            count++;
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }

                        while(!executor.isClosed()) {
                            log.info("TAG:{},WebSocket未断开，继续接受数据中...",1);
                            try {
                                Thread.sleep( 10 * 1000);
                            } catch (Exception e) {
                                log.error(e.getMessage());
                            }
                        }
            });

        }*/
        return jsonObject;
    }
    
    private void getFx678Data()  {
        /*try {
            Thread.sleep( 1 * 1000);
        } catch (Exception e) {
            log.error(e.getMessage());
        }*/

        try {

            long currentTime = System.currentTimeMillis();
            String url = "https://hqjs.fx678.com:9180/socket.io/?EIO=3&transport=polling&t="+currentTime+"-1";
            //String url = "http://www.baidu.com";
            String result ="";
            String sid = "";
            Map<String,String> map = new HashMap<>();
            map.put("Origin","https://quote.fx678.com");
            map.put("Referer","https://quote.fx678.com/");
            map.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
            result = HttpRequestUtil.doGetAndReturnString(url,map);
            log.info("TAG:{},result1 sid :{}",2,result);
            sid = getSid(result);

            if(!StringUtils.isEmpty(sid)){
                long currentTime1 = System.currentTimeMillis();
                url = "https://hqjs.fx678.com:9180/socket.io/?EIO=3&transport=polling&t="+currentTime1+"-5&sid="+sid;
                result = HttpRequestUtil.doGetAndReturnString(url);
                log.info("TAG:{},result5 sid :{}",2,result);

                currentTime1 = System.currentTimeMillis();
                url = "https://hqjs.fx678.com:9180/socket.io/?EIO=3&transport=polling&t="+currentTime1+"-6&sid="+sid;
                result = HttpRequestUtil.sendCPICPostRequest(url,"https://quote.fx678.com/");
                log.info("TAG:{},result6 sid :{}",2,result);

                currentTime1 = System.currentTimeMillis();
                url = "https://hqjs.fx678.com:9180/socket.io/?EIO=3&transport=polling&t="+currentTime1+"-7&sid="+sid;
                result = HttpRequestUtil.doGetAndReturnString(url);
                log.info("TAG:{},result7 sid :{}",2,result);

                currentTime1 = System.currentTimeMillis();
                url = "https://hqjs.fx678.com:9180/socket.io/?EIO=3&transport=polling&t="+currentTime1+"-8&sid="+sid;
                result = HttpRequestUtil.doGetAndReturnString(url);
                log.info("TAG:{},result8 sid :{}",2,result);


                String finalSid = sid;
                log.info("TAG:{},webscoket sid :{}",2,result);
                threadPoolExecutor.submit(() ->{
                    MyWebSocketExecutor executor = new MyWebSocketExecutor();
                    try {
                        String wsUrl = "wss://hqjs.fx678.com:9180/socket.io/?EIO=3&transport=websocket&sid="+ finalSid;
                        log.info("webscoket wsUrl :{}",wsUrl);
                        MyWebSocketClient client = new MyWebSocketClient(wsUrl, executor,2);
                        client.connect();
                        while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
                            log.info("TAG:{},还没有打开,client状态：{}",2,client.getReadyState());
                            Thread.sleep( 3 * 1000);
                        }
                        log.info("TAG:{},建立websocket连接",2);

                        client.send("2probe");
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }

                    /*while(!executor.isClosed()) {
                        log.info("TAG:{},WebSocket未断开，继续接受数据中...",2);
                        try {
                            Thread.sleep( 10 * 1000);
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                    }*/
                    if(executor.isClosed()){
                        log.info("TAG:{},WebSocket断了...",2);
                        try {
                            log.info("TAG:{},WebSocket 1S之后准备重启...",2);
                            Thread.sleep(1000);
                            getFx678Data();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private String getSid(String result){
        String s = "";
        if(!StringUtils.isEmpty(result)) {
            s = JSONObject.parseObject(result.substring(result.indexOf("0{") + 1, result.length())).get("sid").toString();
        }
        return s;
    }

    private void submitData(Map<Object,Object> Array,String name,int tag){
            long startTime = System.currentTimeMillis();
            try {
                if(Array!=null){
                    int size = Array.size();
                    int i=0;
                    for (Map.Entry<Object,Object> entry : Array.entrySet()) {
                        i++;
                        switch (name){
                            case "欧元美元": {
                                redisService.hmSet(RedisKeys.EURUSD.toString().replace(".", ""), entry.getKey(), entry.getValue());
                                break;
                            }
                            case "现货黄金": redisService.hmSet(RedisKeys.GOLD.toString().replace(".",""),entry.getKey(),entry.getValue());break;
                            case "美元人民币": redisService.hmSet(RedisKeys.USDRMB.toString().replace(".",""),entry.getKey(),entry.getValue());break;
                            case "美元指数": redisService.hmSet(RedisKeys.USDINDEX.toString().replace(".",""),entry.getKey(),entry.getValue());break;
                            case "英镑美元": redisService.hmSet(RedisKeys.GBPUSD.toString().replace(".",""),entry.getKey(),entry.getValue());break;
                        }
                        if(i%100==0){
                            log.info("线程：{}，总条数：{}，完成100条迁移，共完成：{}",tag,size,i);
                        }
                    }
                    log.info("线程结束：{},总条数:{},完成条数：{},耗时：{}",tag,size,i,System.currentTimeMillis()-startTime);
                    //
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }

    }

    @Override
    public void testCache() {
        Date date = new Date();
        String dateStr = DateUtils.format(date, Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
        guavaCacheService.put(dateStr,date.getTime());
        //打印元素
        guavaCacheService.printAllCacheValue();
    }

    @Override
    public void deleteFristCache() {
        Object o = guavaCacheService.getFirstCacheValue();
        log.info("获取第一个元素！！！");
        guavaCacheService.deleteFristCacheValue();

    }


    private String getHtml(long timeStemp){
        String json = "";
        try {
            //1.创建连接client
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            //2.设置连接的相关选项
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setTimeout(10000);

            String url = "https://dataapi.2rich.net/InterfaceCollect/Default.aspx?Code=fx168&bCode=IQuoteDataALL&succ_callback=show_whdataHot&_=" + timeStemp;
            URL link=new URL(url);
            WebRequest request=new WebRequest(link);

            //设置请求头
            request.setAdditionalHeader("authority", "dataapi.2rich.net");
            request.setAdditionalHeader("method", "GET");
            String path = "/InterfaceCollect/Default.aspx?Code=fx168&bCode=IQuoteDataALL&succ_callback=show_whdataHot&_="+timeStemp;
            request.setAdditionalHeader("path", path);
            request.setAdditionalHeader("scheme", "https");
            request.setAdditionalHeader("accept", "*/*");
            request.setAdditionalHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36 Maxthon/5.2.3.4000");
            request.setAdditionalHeader("accept-encoding", "gzip, deflate");
            request.setAdditionalHeader("accept-language", "zh-CN");
            request.setAdditionalHeader("dnt", "1");
            request.setAdditionalHeader("referer", "https://forex.fx168.com/");
            HtmlPage page = webClient.getPage(request);
            WebResponse response = page.getWebResponse();
            //if (response.getContentType().equals("application/json")) {
            json = response.getContentAsString();
            if(!StringUtils.isEmpty(json)){
                log.info("获取数据成功！");
            }else{
                log.error("获取数据失败！");
            }
        }catch(Exception e){
            log.error(e.getMessage());
        }finally {
            return json;
        }
    }

    private List<TradeItem> json2TradeItem(String json){
        List<TradeItem> tradeItems = JSON.parseArray(json,TradeItem.class);
        return tradeItems;
    }

    private void crawlPageWithoutAnalyseJs(int isProxy) {
        log.info("***********************begin***********************isProxy:{}",isProxy);
        long time = System.currentTimeMillis();
        String cks = "";
        try {
            URL link=new URL(realUrl);
            WebRequest request=new WebRequest(link);
            //1.创建连接client
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            //2.设置连接的相关选项
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setTimeout(10000);
            ProxyConfig proxyConfig = webClient.getOptions().getProxyConfig();
            if(isProxy==1) {
                proxyConfig.setProxyHost("127.0.0.1");
                proxyConfig.setProxyPort(1080);
            }
            webClient.getCookieManager().setCookiesEnabled(true);//开启cookie管理
            //webClient.getOptions().setJavaScriptEnabled(true);//开启js解析。对于变态网页，这个是必须的
            //webClient.getOptions().setCssEnabled(true);//开启css解析。对于变态网页，这个是必须的。

            //3.抓取页面
            HtmlPage page = webClient.getPage(request);
            //webClient.waitForBackgroundJavaScript(10000);

            //获取cookie
            CookieManager CM = webClient.getCookieManager();
            Set<Cookie> cookies = CM.getCookies();//返回的Cookie在这里，下次请求的时候可能可以用上啦。
            String cookieStr = "";
            for(Cookie c : cookies) {
                cks = cks+c.getName()+"="+c.getValue()+";";
                if(c.getName().equals("ASP.NET_SessionId")){
                    cookieStr = c.getName()+"="+c.getValue();
                }
            }
            log.info("cks:{}",cks);
            log.info("cookieStr:{}",cookieStr);
            //拿到cookie之后请求目标数据json
            long timeStemp = System.currentTimeMillis();
            String url = "https://dataapi.2rich.net/InterfaceCollect/Default.aspx?Code=fx168&bCode=IQuoteDataALL&succ_callback=show_whdataHot&_="+timeStemp;
            link=new URL(url);
            request=new WebRequest(link);
            //设置请求头
            request.setAdditionalHeader("authority", "dataapi.2rich.net");
            request.setAdditionalHeader("method", "GET");
            String path = "/InterfaceCollect/Default.aspx?Code=fx168&bCode=IQuoteDataALL&succ_callback=show_whdataHot&_="+timeStemp;
            request.setAdditionalHeader("path", path);
            request.setAdditionalHeader("scheme", "https");
            request.setAdditionalHeader("accept", "*/*");
            request.setAdditionalHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36 Maxthon/5.2.3.4000");
            request.setAdditionalHeader("accept-encoding", "gzip, deflate");
            request.setAdditionalHeader("accept-language", "zh-CN");
            request.setAdditionalHeader("dnt", "1");
            request.setAdditionalHeader("referer", "https://forex.fx168.com/");
            request.setAdditionalHeader("cookie", cookieStr);
            page = webClient.getPage(request);
            WebResponse response = page.getWebResponse();
            //if (response.getContentType().equals("application/json")) {
            String json = response.getContentAsString();
            log.info("json:{}",json);
            //}
            webClient.close();
            if (!StringUtils.isEmpty(cks)) {
                log.info("获取cookie耗时："+(System.currentTimeMillis()-time));
            }else {
                log.info("*******获取cookie失败，耗时："+(System.currentTimeMillis()-time)+"******");
            }
        } catch (Exception e) {
            log.error("通过htmlunit获取cookie失败......" , e);
        }finally {
        }
    }

}
