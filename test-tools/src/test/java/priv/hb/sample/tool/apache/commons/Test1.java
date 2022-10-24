package priv.hb.sample.tool.apache.commons;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * @author hubin
 * @date 2022年10月13日 15:20
 */
public class Test1 {

    @Test
    public void testBlankAndEmpty() {
        // 都为true为空
        System.out.println(isBlank(" "));
        System.out.println(isBlank(""));
        System.out.println(isBlank(null));
        System.out.println(isBlank(" \t  \r\n  \n   "));
        System.out.println("================");


        System.out.println(isEmpty(" "));   //false
        System.out.println(isEmpty(""));
        System.out.println(isEmpty(null));
        System.out.println(isEmpty(" \t  \r\n  \n   "));  //false
        System.out.println(isEmpty("hb"));   //false

    }


    /**
     * 字符串补齐测试
     */
    @Test
    public void test2() {

        // 右边自动补齐。
        System.out.println(StringUtils.rightPad(null, 1, "*"));
        System.out.println(StringUtils.rightPad("hb", 5, ' '));
        System.out.println(StringUtils.rightPad("hb", 1, ' '));
        System.out.println(StringUtils.rightPad("hb", -1, ' '));

        // 左边自动补齐
        StringUtils.leftPad(null, 1, "*");    //  =null


        // 左右补齐
        StringUtils.center(null, 1);       //  = null
        StringUtils.center("", 4);         // = "    "
        StringUtils.center("ab", -1);      // = "ab"
        StringUtils.center("ab", 4);       // = " ab "
        StringUtils.center("abcd", 2);     // = "abcd"
        StringUtils.center("a", 4);        // = " a  "

    }

    @Test
    public void testExec() {




    }

}
