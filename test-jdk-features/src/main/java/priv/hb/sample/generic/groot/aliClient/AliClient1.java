package priv.hb.sample.generic.groot.aliClient;

import lombok.Data;

/**
 * @author hubin
 * @date 2022年11月23日 09:59
 */

@Data
public class AliClient1 {
    private String ak;
    private String sk;
    private String token;


    public AliClient1(String ak, String sk, String token) {
        this.ak = ak;
        this.sk = sk;
        this.token = token;
    }


    public void echo() {
        System.out.println("我是阿里云测试客户端1," + this);
    }
}
