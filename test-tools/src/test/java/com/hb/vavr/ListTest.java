package com.hb.vavr;

import org.junit.jupiter.api.Test;

import io.vavr.collection.Iterator;
import io.vavr.collection.List;

/**
 * @author hubin
 * @date 2022年10月13日 10:25
 */
public class ListTest {

    @Test
    public void test1() {


    }

    /**
     * 使用sliding滑动窗口,将一个集合中的数据,按数据量来拆分成多个List.
     */
    @Test
    public void test2() {
        List<Integer> range = List.range(0, 100);
        System.out.println(range.size());
        java.util.List<List<Integer>> sliding = range.sliding(30, 30).toJavaList();
        System.out.println(sliding);
    }
}
