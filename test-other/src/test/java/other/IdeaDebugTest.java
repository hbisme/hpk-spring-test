package other;

import net.bytebuddy.implementation.bytecode.Throw;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class IdeaDebugTest {

    /**
     * 测试Idea的条件断点
     * 在"System.out.println(integer);"上设置"条件断点"
     */
    @Test
    public void test1() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        for (Integer integer : list) {
            System.out.println(integer);
        }
    }

    /**
     * 测试Idea的"强制返回值" 和 "模拟异常"
     * 在 test22()的"return flag;"前设置断点,进入断点后,选择"Force Return"或者"Throw Exception",
     * 就可以强制方法返回"true";
     */
    @Test
    public void test2() {
        boolean flag = false;
        boolean res = test22(flag);
        System.out.println("res: " + res);
    }

    public boolean test22(boolean flag) {
        System.out.println("flag: " + flag);
        return flag;
    }

    /**
     * 测试idea的异常断点
     */
    @Test
    public void test3() {
        boolean flag = false;
        throw new NullPointerException();
    }


}
