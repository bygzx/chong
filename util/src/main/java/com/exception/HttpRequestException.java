package com.exception;

/**
 * @author bygzx
 * @date 2019/5/13 10:26
 **/
public class HttpRequestException extends Exception {

    private static final long serialVersionUID = -8276447232195112507L;

    public HttpRequestException() {
        super();
    }

    public HttpRequestException(String message) {
        super(message);
    }

    public HttpRequestException(String message, Throwable cause) {
        super(message , cause);
    }

}
