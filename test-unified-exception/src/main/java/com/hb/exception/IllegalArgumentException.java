package com.hb.exception;

/**
 * 参数校验失败异常
 */
public class IllegalArgumentException extends YTException {

    private static final long serialVersionUID = 1L;

    public IllegalArgumentException(String message) {
        super(message);
    }
}
