package com.hb;

import com.hb.dto.TestDTO2;

import org.junit.Test;

/**
 * @author hubin
 * @date 2022年08月10日 19:14
 */
public class Test1 {
    @Test
    public void test1() {
        System.out.println("123");
        TestDTO2 testDTO2 = new TestDTO2();
        testDTO2.setEmail("123123");
        System.out.println(testDTO2);
    }
}
