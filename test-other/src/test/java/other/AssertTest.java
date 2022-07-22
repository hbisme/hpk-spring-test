package other;

import org.junit.Test;

import java.util.ArrayList;

import org.springframework.util.Assert;

public class AssertTest {
    @Test
    public void test1() {
        Assert.isNull(null, "检查对象不为null告警");
        Assert.notNull(123, "检查对象为null告警");
        Assert.isTrue( 1 > 0 , "校验规则失败告警");
        Assert.doesNotContain("hb", "hb is ok", "文字未包含异常");
        Assert.hasLength("字符串长度必须大于等于1", "字符串长度小于1告警");
        Assert.hasText("非空字符的字符串长度必须大于等于1", "非空字符的字符串长度小于1告警");
        Assert.noNullElements(new Object[]{1,2,3}, "数组为空异常");
        Assert.notEmpty(new ArrayList<Object>(), "list元素为空告警");

    }

}


