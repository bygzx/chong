package com.util.controller;

/**
 * @author bygzx
 * @date 2019/5/13 10:29
 **/
public class AbstractController {

    public static CommonResult<Object> buildSuccess() {
        return CommonResult.build(CommonResultCode.SUCCESS.code, CommonResultCode.SUCCESS.msg, new Object());
    }
    public static <T> CommonResult<T> buildSuccess(T data) {
        return CommonResult.build(CommonResultCode.SUCCESS.code, CommonResultCode.SUCCESS.msg, data);
    }

    public static CommonResult<Object> buildFailed() {
        return CommonResult.build(CommonResultCode.SYS_BUSINESS_ERROR.code, CommonResultCode.SYS_BUSINESS_ERROR.msg, new Object());
    }

    public static <T> CommonResult<T> buildFailed(T data) {
        return CommonResult.build(CommonResultCode.SYS_BUSINESS_ERROR.code, CommonResultCode.SYS_BUSINESS_ERROR.msg, data);
    }

    public static CommonResult<Object> buildFailed(CommonResultCode errCode) {
        return CommonResult.build(errCode.code, errCode.msg, new Object());
    }

    public static <T> CommonResult<T> buildFailed(CommonResultCode errCode,T data) {
        return CommonResult.build(errCode.code, errCode.msg, data);
    }

    public static CommonResult<Object> buildFailed(CommonResultCode errCode,String errMsg) {
        return CommonResult.build(errCode.code, errMsg, new Object());
    }

    public static <T> CommonResult<T> build(int code, String msg, T data) {
        return CommonResult.build(code, msg, data);
    }
}
