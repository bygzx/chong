package com.dto;

import lombok.Data;

/**
 * @author bygzx
 * @date 2018/9/2 14:05
 **/
@Data
public class MpWechatConversationRequestDTO {

    private String MediaId;
    private long CreateTime;
    private String ToUserName;
    private String FromUserName;
    //事件类型，subscribe(订阅)、unsubscribe(取消订阅)
    private String Event;
    private String EventKey;
    private String Ticket;
    private long MsgId;
    //miniprogrampage-卡片消息 image-图片消息 text-文本消息
    private String MsgType;
    //以下属性是 image 的
    private String PicUrl;
    //以下属性是 text 的
    private String Content;
    //以下属性是 miniprogrampage 的
    private String AppId;
    private String PagePath;
    private String ThumbUrl;
    private String Title;
    private String Encrypt;

    //接入客服的属性
    private String signature;
    private String timestamp;
    private String nonce;
    private String openid;
    private String encrypt_type;
    private String msg_signature;
    private String postData;
    //指菜单ID，如果是个性化菜单，则可以通过这个字段，知道是哪个规则的菜单被点击了
    private String MenuId;
}
