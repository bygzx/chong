package com.papa.util.constant;

/**
 * @author bygzx
 * @date 2019/5/14 14:32
 **/
public enum RedisKeys {

    /** 手机号验证码， key:前缀 + mobile, val.smsCode */
    PHONE_CHECK_CODE_KEY("xx.user.smsCheckCode"),

    /** 用户token, key: 前缀 + uid, val. token*/
    USER_TOKEN_KEY("xx.user.token"),
    /** 分钟redis结束时间*/
    MIN_CLOSE_PRICE("min.close.price."),
    /** MA*/
    MA5("MA5."),
    MA10("MA10."),
    MA15("MA15."),
    MA30("MA30."),
    MA60("MA60."),
    MA120("MA120."),
    MA240("MA240."),


    GOLD("GOLD."),
    USDINDEX("USDINDEX."),
    EURUSD("EURUSD."),
    GBPUSD("GBPUSD."),
    USDRMB("USDRMB.")
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
