package priv.hb.sample.tool.hutool;

import org.junit.jupiter.api.Test;

import cn.hutool.core.lang.Validator;

/**
 * 手机,email,名称验证
 * @author hubin
 * @date 2022年10月14日 09:41
 */
public class ValidatorTest {

    @Test
    public void test1() {

        boolean isEmail = Validator.isEmail("loolly@gmail.com");

        boolean isPhone = Validator.isMobile("13738152120");

        // 验证是否为英文字母 、数字和下划线
        boolean hb = Validator.isGeneral("hbA_123");


        // 因为内容中包含非中文字符，因此会抛出ValidateException。
        Validator.validateChinese("我是一段zhongwen", "内容中包含非中文");


    }
}
