package com.hb.guava.collection;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.junit.jupiter.api.Test;

/**
 * 双向map测试, 可以通过value获取map的key
 * @author hubin
 * @date 2022年10月13日 14:57
 */
public class BiMapTest {
    @Test
    public void test1() {

        BiMap<String, String> weekNameMap = HashBiMap.create();
        weekNameMap.put("星期一", "Monday");
        weekNameMap.put("星期二", "Tuesday");
        weekNameMap.put("星期三", "Wednesday");
        weekNameMap.put("星期四", "Thursday");
        weekNameMap.put("星期五", "Friday");
        weekNameMap.put("星期六", "Saturday");
        weekNameMap.put("星期日", "Sunday");

        System.out.println("星期日的英文名是" + weekNameMap.get("星期日"));
        System.out.println("Sunday的中文是" + weekNameMap.inverse().get("Sunday"));

    }


}
