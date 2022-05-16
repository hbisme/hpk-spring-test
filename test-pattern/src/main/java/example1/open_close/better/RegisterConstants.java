package example1.open_close.better;

/**
 * 服务的静态变量容器
 */
public class RegisterConstants {


    public enum RegisterEnum {
        GZH_CHANNEL(0, "公众号渠道"),
        APP_CHANNEL(1, "app渠道");


        RegisterEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        int code;
        String desc;

        public int getCode() {
            return code;
        }
    }
}
