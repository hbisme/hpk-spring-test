package com.hb.hutool;

import org.junit.jupiter.api.Test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;

/**
 * 二维码测试
 * @author hubin
 * @date 2022年10月14日 10:30
 */
public class QrCodeUtilTest {

    @Test
    public void test1() {
        // 生成指定url对应的二维码到文件，宽和高都是300像素
        QrCodeUtil.generate("https://hutool.cn/", 300, 300, FileUtil.file("/Users/hubin/work/ideaProject/myIdeaProjects/hpk-spring-test/test-tools/src/test/resources//qrcode.jpg"));

    }

}
