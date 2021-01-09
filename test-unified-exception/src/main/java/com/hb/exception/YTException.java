package com.hb.exception;

import com.hb.enums.BaseExceptionEnum;

public class YTException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String code;

    public YTException(int code, String message) {
        super(message);
        this.code = String.valueOf(code);
    }

    public YTException(BaseExceptionEnum ex) {
        super(ex.getMsg());
        this.code = String.valueOf(ex.getCode());
    }

    public YTException(String message) {
        this(BaseExceptionEnum.SYSTEM_ERROR.getCode(), message);
    }

}
