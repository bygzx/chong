package com.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.config.GetSpringBean;
import com.dto.*;
import com.exception.HttpRequestException;
import com.redis.RedisService;
import com.util.http.HttpRequestUtil;
import com.util.lang.EmojiUtils;
import com.weixin.AES.AesException;
import com.weixin.AES.WXBizMsgCrypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author eric
 * @date 2019/7/15 14:25
 **/
@Slf4j
public class WeiXinUtil {

    private static final String GET_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=@appid&secret=@secret";

    public static String getAppId(){
        RedisService redisService = GetSpringBean.getBean(RedisService.class);
        return redisService.get("appId").toString();
    }

    public static String getSecret(){
        RedisService redisService = GetSpringBean.getBean(RedisService.class);
        return redisService.get("secret").toString();
    }

    private static String getAccessToken() throws HttpRequestException {
        String token = "";
        RedisService redisService = GetSpringBean.getBean(RedisService.class);
        if(redisService!=null){
            if(redisService.get("token")!=null) {
                token = redisService.get("token").toString();
            }
            if(StringUtils.isEmpty(token)){
                String appId = redisService.get("appId").toString();
                String secret = redisService.get("secret").toString();
                if(!StringUtils.isEmpty(appId)&&!StringUtils.isEmpty(secret)){
                    String url = GET_TOKEN_URL;
                    url = url.replaceAll("@appid" , appId);
                    url = url.replaceAll("@secret" , secret);

                    JSONObject responseJson = HttpRequestUtil.getRequestByJson(url);
                    log.info("get access_token from weixin server response={} url={}" , responseJson, url);
                    if (responseJson != null && responseJson.containsKey("access_token")) {
                        AccessTokenDto accessTokenDto = new AccessTokenDto();
                        accessTokenDto.setAccessToken(responseJson.getString("access_token"));
                        accessTokenDto.setAppId(appId);
                        long currentTime = System.currentTimeMillis();
                        accessTokenDto.setCreateTimestamp(currentTime);
                        long expiresIn = responseJson.getIntValue("expires_in");
                        accessTokenDto.setExpiresIn(expiresIn);
                        //提前五分钟失效
                        accessTokenDto.setExpireTimeStamp(currentTime + (expiresIn-5*60)*1000);
                        //设置redis信息
                        redisService.set("token" , JSONObject.toJSONString(accessTokenDto) , (expiresIn-5*60));
                        token = accessTokenDto.getAccessToken();
                    }
                }
            }else {
                AccessTokenDto accessTokenDto = JSON.parseObject(token,AccessTokenDto.class);
                if(accessTokenDto!=null&& !StringUtils.isEmpty(accessTokenDto.getAccessToken())){
                    token = accessTokenDto.getAccessToken();
                }
            }

        }
        return token;
    }

    /**
     * 将加密后的原文进行解密重新封装
     * @param originalXml 原xml
     * @return    重新解密后的xml
     */
    public static String  decryptMsg(String timestamp,String nonce,String originalXml,String msg_signature,String EncodingAESKey,String token,String AppID) {

        // 微信加密签名
        //String sVerifyMsgSig = request.getParameter("signature");
        //String msgSignature = request.getParameter("msg_signature");
        // 时间戳
        //String timestamp = request.getParameter("timestamp");
        // 随机数
        //String nonce = request.getParameter("nonce");
        try {
            WXBizMsgCrypt pc = new WXBizMsgCrypt(token, EncodingAESKey, AppID);
            return pc.decryptMsg(msg_signature, timestamp, nonce, originalXml);
        } catch (AesException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String  decryptMsg(String timestamp,String nonce,String originalXml,String msg_signature) throws HttpRequestException {

        // 微信加密签名
        //String sVerifyMsgSig = request.getParameter("signature");
        //String msgSignature = request.getParameter("msg_signature");
        // 时间戳
        //String timestamp = request.getParameter("timestamp");
        // 随机数
        //String nonce = request.getParameter("nonce");
        try {
            WXBizMsgCrypt pc = new WXBizMsgCrypt(getAccessToken(), getSecret(), getAppId());
            return pc.decryptMsg(msg_signature, timestamp, nonce, originalXml);
        } catch (AesException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    //回复内容格式
    //text
    private static String textStr = "{ \"touser\":\"OPENID\",\"msgtype\":\"text\", \"text\": {  \"content\":\"contentStr\" }}";
    //img
    private static String imgStr = "{\"touser\":\"OPENID\",\"msgtype\":\"image\",\"image\":{\"media_id\":\"MEDIAID\"}}";
    //news
    private static String newsStr = "{\"touser\":\"OPENID\",\"msgtype\":\"mpnews\",\"mpnews\":{\"media_id\":\"MEDIAID\"}}";
    /**

     * 组装菜单

     * @return

     */

    public static Menu initMenu(){

        Menu menu = new Menu();

        ClickButton button11 = new ClickButton();

        button11.setName("了解杰瑞教育");

        button11.setType("click");

        button11.setKey("11");



        ClickButton button12 = new ClickButton();

        button12.setName("加入杰瑞教育");

        button12.setType("click");

        button12.setKey("12");



        ViewButton button21 = new ViewButton();

        button21.setName("杰瑞教育官网");

        button21.setType("view");

        button21.setUrl("http://www.jerehedu.com");



        ViewButton button22 = new ViewButton();

        button22.setName("杰瑞教育新闻网");

        button22.setType("view");

        button22.setUrl("http://www.jredu100.com");



        ClickButton button31 = new ClickButton();

        button31.setName("杰小瑞");

        button31.setType("click");

        button31.setKey("31");



        Button button1 = new Button();

        button1.setName("杰瑞教育"); //将11/12两个button作为二级菜单封装第一个一级菜单

        button1.setSub_button(new Button[]{button11,button12});



        Button button2 = new Button();

        button2.setName("相关网址"); //将21/22两个button作为二级菜单封装第二个二级菜单

        button2.setSub_button(new Button[]{button21,button22});



        menu.setButton(new Button[]{button1,button2,button31});// 将31Button直接作为一级菜单

        return menu;

    }

    public static String formatText(String openId,String text){
        String textStrs = textStr.replaceAll("OPENID",openId);
        textStrs = textStrs.replaceAll("contentStr",text);
        textStrs = EmojiUtils.emojiRecovery(textStrs);
        return textStrs;
    }

    public static String formatImg(String openId,String mediaId){
        String imgStrs = imgStr.replaceAll("OPENID",openId);
        imgStrs = imgStrs.replaceAll("MEDIAID",mediaId);
        return imgStrs;
    }

    public static String formatNews(String openId,String mediaId){
        String newsStrs = newsStr.replaceAll("OPENID",openId);
        newsStrs = newsStrs.replaceAll("MEDIAID",mediaId);
        return newsStrs;
    }

    public static void sentWxMsg(String type,String openId,String content) {
        String accessToken = null;
        try {
            accessToken = getAccessToken();
            log.info("[WeiXinUtil] sentWxMsg type-{},openId-{},content-{},accessToken-{}", type, openId, content, accessToken);
            if(StringUtils.isEmpty(accessToken)){
                log.error("************************* 获取accessToken失败，请检查redis *************************");
                return;
            }

            String msg = "";
            switch (type) {
                case "text":
                    msg = formatText(openId, content);
                    break;
                case "image":
                    msg = formatImg(openId, content);
                    break;
                case "news":
                    msg = formatNews(openId, content);
                    break;
                default:
                    break;
            }
            //发消息
            if (!"".equals(msg)) {
                try {
                    JSONObject result = new JSONObject();
                    String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
                    requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
                    result = JSONObject.parseObject(msg);
                    JSONObject js = HttpRequestUtil.postRequestByJson(requestUrl, result);
                    log.info("[WeiXinUtil] sentWxMsg JSONObject-{}", js.toJSONString());
                } catch (Exception e) {
                    log.error("[WeiXinUtil] sentWxMsg error:");
                    log.error(e.getMessage());
                }
            }
        } catch (HttpRequestException e) {
        e.printStackTrace();
    }
    }
}
