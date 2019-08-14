package com.wx.proxy.controller;


import com.dto.MpWechatConversationRequestDTO;
import com.exception.HttpRequestException;
import com.util.controller.AbstractController;
import com.weixin.WeiXinUtil;
import com.weixin.XstreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author bygzx
 * @date 2018/8/14 14:58
 * 公众号
 **/
@Slf4j
@RestController
@RequestMapping("/gzh/conversation")

public class GzhConversationController extends AbstractController {

    /**
     *  域名认证用
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getConversationFromWx", method = RequestMethod.GET)
    public Object getConversationFromWx(String signature, String timestamp, String nonce, String echostr, HttpServletResponse rsp) {
        log.info("get Content from WX:signature-{},timestamp-{}, nonce-{},echostr-{},appId-{}",signature,timestamp,nonce,echostr);
        return ResponseEntity.ok().
                header("Content-Type", "text/plain").
                body(echostr);
    }


    /**
     * 接收微信传过来的消息
     * @param mpWechatConversationRequestDTO
     * @return
     * @throws UnsupportedEncodingException
     * @throws HttpRequestException
     */
    @ResponseBody
    @RequestMapping(value = "/getConversationFromWx", method = RequestMethod.POST,produces = {"application/xml; charset=UTF-8"})
    public Object getConversationFromWx(MpWechatConversationRequestDTO mpWechatConversationRequestDTO,
                                        @RequestBody String postData, HttpServletResponse response, HttpServletRequest request) throws   HttpRequestException {
        log.info("postData from WX:{}",postData);
        String ss = "";
        MpWechatConversationRequestDTO mpWechatConversationRequestDTO1 = (MpWechatConversationRequestDTO) XstreamUtil.xmlToBean(postData,MpWechatConversationRequestDTO.class);
        if(mpWechatConversationRequestDTO1.getEncrypt()!=null && !"".equals(mpWechatConversationRequestDTO1.getEncrypt())){
            //先解密
            String resp = WeiXinUtil.decryptMsg(mpWechatConversationRequestDTO.getTimestamp(),mpWechatConversationRequestDTO.getNonce(),postData,
                    mpWechatConversationRequestDTO.getMsg_signature());
            if(resp!=null && !"".equals(resp)){
                log.info("接收到微信的加密消息：{}",resp);
            }
        }else {
            log.info("接收到微信的未加密消息：{}",mpWechatConversationRequestDTO1.toString());

        }
        return "success";
    }

    /**
     * 推消息
     * @param type  text
     * @param openId
     * @param content
     * @return
     */
    @RequestMapping(value = "/sentMsg", method = RequestMethod.POST)
    public Object sentMsg(String type,String openId,String content) {
        log.info("text to WX:{}",content);
        try{
            WeiXinUtil.sentWxMsg(type, openId, content);
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return "success";
    }

    @RequestMapping(value = "/sent", method = RequestMethod.GET)
    public Object sent(String content) {
        log.info("text to WX:{}",content);
        String appId = WeiXinUtil.getAppId();

        return "success";
    }



}
