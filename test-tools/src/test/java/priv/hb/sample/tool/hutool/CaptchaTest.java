package priv.hb.sample.tool.hutool;

import org.junit.jupiter.api.Test;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.Console;

/**
 * 图像验证码工具类
 * @author hubin
 * @date 2022年10月14日 10:24
 */
public class CaptchaTest {
    @Test
    public void test1() {
        //定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);

        //图形验证码写出，可以写出到文件，也可以写出到流
        lineCaptcha.write("/Users/hubin/work/ideaProject/myIdeaProjects/hpk-spring-test/test-tools/src/test/resources/line.png");

        //输出code
        Console.log(lineCaptcha.getCode());
        //验证图形验证码的有效性，返回boolean值
        lineCaptcha.verify("1234");

        //重新生成验证码
        // lineCaptcha.createCode();
        // lineCaptcha.write("d:/line.png");
        //新的验证码
        // Console.log(lineCaptcha.getCode());
        //验证图形验证码的有效性，返回boolean值
        // lineCaptcha.verify("1234");

    }
}
