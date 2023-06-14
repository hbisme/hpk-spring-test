package priv.hb.sample.generic.groot.aliClient;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年11月23日 09:59
 */
@Data
public class AliClient2 {
    private String ak;
    private String sk;
    private String token;
    private String endPoint;


    public AliClient2(String ak, String sk, String token, String endPoint) {
        this.ak = ak;
        this.sk = sk;
        this.token = token;
        this.endPoint = endPoint;
    }


    public void echo() {
        System.out.println("我是阿里云测试客户端2," + this);
    }
}
