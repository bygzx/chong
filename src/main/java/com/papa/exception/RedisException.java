package com.papa.exception;

/**
 * @author bygzx
 * @date 2019/5/14 14:22
 **/
public class RedisException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RedisException() {
        super();
    }

    public RedisException(String message) {
        super(message);
    }

    public RedisException(Throwable cause) {
        super(cause);
    }

    public RedisException(String message, Throwable cause) {
        super(message , cause);
    }

}
