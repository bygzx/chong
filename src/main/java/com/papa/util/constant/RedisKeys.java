package com.papa.util.constant;

/**
 * @author bygzx
 * @date 2019/5/14 14:32
 **/
public enum RedisKeys {

    /** 手机号验证码， key:前缀 + mobile, val.smsCode */
    PHONE_CHECK_CODE_KEY("xx.user.smsCheckCode"),

    /** 用户token, key: 前缀 + uid, val. token*/
    USER_TOKEN_KEY("xx.user.token")
    ;

    private String prefix;
    RedisKeys(String prefix) {
        this.prefix = prefix;
    }

    public String getKey(Object... args) {
        if (args.length < 1) {
            return this.prefix;
        }

        StringBuilder sb = new StringBuilder(this.prefix);
        for (Object arg : args) {
            sb.append(".").append(arg);
        }

        return sb.toString();
    }
}
