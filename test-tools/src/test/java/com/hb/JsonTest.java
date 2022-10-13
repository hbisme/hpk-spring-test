package com.hb;

import com.alibaba.fastjson.JSON;
import com.hb.pojo.User;

import org.junit.jupiter.api.Test;

import io.vavr.Tuple2;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;

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
