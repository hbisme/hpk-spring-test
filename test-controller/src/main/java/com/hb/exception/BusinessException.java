package com.hb.exception;

/**
 * 自定义异常
 * @author hubin
 * @date 2022年08月10日 19:21
 */
public class BusinessException extends RuntimeException{
    public BusinessException(String message) {
        super(message);
    }
}
