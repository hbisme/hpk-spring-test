package priv.hb.sample.tool;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author hubin
 * @date 2022年09月20日 13:53
 */
public class StringUtilTest {
    @Test
    public void testSubstringBetween1() {
        String str1 = "select a,b,c from t1";
        String select = StringUtils.substringBetween(str1, "select", "from");
        System.out.println(select);
        Assert.assertEquals(select, " a,b,c ");
    }


    @Test
    public void testSubstringBetween2() {
        String str1 = "select * from (select a,b,c from t1)a";
        // 会匹配第一个遇到的select 和from之间的*
        String select = StringUtils.substringBetween(str1, "select", "from");
        System.out.println(select);
        Assert.assertEquals(select, " * ");
    }


    @Test
    public void testBlankAndEmpty() {
        StringUtils.isBlank(null);      //= true
        StringUtils.isBlank("");        //= true
        StringUtils.isBlank(" ");      // = true
        StringUtils.isBlank("bob");    // = false

        StringUtils.isEmpty(null);    //  = true
        StringUtils.isEmpty("");      // = true
        StringUtils.isEmpty(" ");      // = false
        StringUtils.isEmpty("bob");   //  = false
    }

    @Test
    public void testLeftAndRight() {
        StringUtils.left("abc", 2);  // = "ab"
        StringUtils.left("abc", 4);  // = "abc"

        StringUtils.right("abc", 2);  // = "bc"
        StringUtils.right("abc", 4);  // = "abc"


    }


}
