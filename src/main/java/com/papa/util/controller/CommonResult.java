package com.papa.util.controller;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author bygzx
 * @date 2019/5/13 10:29
 **/
public class CommonResult<T> {
    private int code;
    private String msg;
    private T data;

    @JSONField(serialize = false)
    public boolean isSuccess() {
        return this.code == CommonResultCode.SUCCESS.code;
    }

    public static CommonResult<Void> buildSuccess() {
        return build(CommonResultCode.SUCCESS.code, CommonResultCode.SUCCESS.msg, null);
    }
    public static <T> CommonResult<T> buildSuccess(T data) {
        return build(CommonResultCode.SUCCESS.code, CommonResultCode.SUCCESS.msg, data);
    }

    public static <T> CommonResult<T> buildFailed(CommonResultCode errCode) {
        return build(errCode.code, errCode.msg, null);
    }

    public static <T> CommonResult<T> buildFailed(CommonResultCode errCode, T data) {
        return build(errCode.code, errCode.msg, data);
    }

    public static <T> CommonResult<T> build(int code, String msg, T data) {
        CommonResult<T> ret = new CommonResult<>();
        ret.code = code;
        ret.msg = msg;
        ret.data = data;

        return ret;
    }

    /**
     * 获取code /result  兼容
     * @return
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取code /result  兼容
     * @return
     */
    public int getResult(){
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
    @Override
    public String toString() {
        return String.format("CommonResult {code:%s, msg:%s, data:%s}", code, msg, data);
    }
}
