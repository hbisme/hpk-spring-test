package example1.open_close.better;

/**
 * 注册用户输入参数类
 */
public class RegisterInputParam {
    private int source;
    private long userId;

    public RegisterInputParam(int source, long userId) {
        this.userId = userId;
        this.source = source;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }
}
