package regex;

import org.junit.Test;

import java.util.regex.Pattern;

public class Test1 {

    @Test
    public void test1() {
        String content = "I am noob " +
                "from runoob.com.";

        String pattern = ".*runoob.*";

        boolean isMatch = Pattern.matches(pattern, content);
        System.out.println("字符串中是否包含了 'runoob' 子字符串? " + isMatch);
    }
}
