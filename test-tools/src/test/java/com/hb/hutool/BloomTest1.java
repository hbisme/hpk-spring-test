package com.hb.hutool;

import org.junit.jupiter.api.Test;

import cn.hutool.bloomfilter.BitMapBloomFilter;

/**
 * @author hubin
 * @date 2022年10月14日 11:16
 */
public class BloomTest1 {
    @Test
    public void test1() {
        // 初始化 注意 构造方法的参数大小10 决定了布隆过滤器BitMap的大小
        BitMapBloomFilter filter = new BitMapBloomFilter(10);

        filter.add("123");
        filter.add("abc");
        filter.add("ddd");

        // 查找
        System.out.println(filter.contains("abc"));
        System.out.println(filter.contains("hb"));
    }
}
