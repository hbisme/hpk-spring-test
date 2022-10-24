package priv.hb.sample.tool;

import com.alibaba.fastjson.JSON;


import org.junit.jupiter.api.Test;

import io.vavr.collection.List;
import priv.hb.sample.tool.vavr.pojo.User;

/**
 * @author hubin
 * @date 2022年10月12日 17:47
 */
public class JsonTest {

    @Test
    public void test1() {
        User hb = new User(1, "hb");
        User fsl = new User(2, "fsl");
        java.util.List<User> users = List.of(hb, fsl).toJavaList();
        String jsonString = JSON.toJSONString(users);
        System.out.println(jsonString);
    }


}
