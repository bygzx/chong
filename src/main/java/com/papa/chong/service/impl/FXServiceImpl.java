package com.papa.chong.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.papa.chong.service.FXService;
import com.papa.entity.TradeItem;
import com.papa.exception.HttpRequestException;
import com.papa.redis.RedisService;
import com.papa.util.http.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.URL;
import java.util.*;

/**
 * @author eric
 * @date 2019/7/9 12:34
 **/
@Service
@Slf4j
public class FXServiceImpl implements FXService {

    @Resource
    private RedisService redisService;

    private static String realUrl = "https://forex.fx168.com/";

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
                            ||tradeItem.getTradeName().equals("美元人民币"))
                    redisService.hmSet(tradeItem.getTradeName(),String.valueOf(timeStemp),tradeItem.getTradePrice());
                    //log.info(tradeItem.toString());
                }
            }
        }
        return null;
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
            log.info("json:{}", json);
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
