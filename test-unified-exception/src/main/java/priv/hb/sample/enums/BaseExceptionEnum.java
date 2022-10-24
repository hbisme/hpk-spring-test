package priv.hb.sample.enums;

public enum BaseExceptionEnum {
    PARAMETER_ERROR(400, "参数错误"),
    SYSTEM_ERROR(500, "参数错误"),
    USER_UN_KNOW(1101, "用户不存在"),
    ;

    private int code;
    private String msg;

    BaseExceptionEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
