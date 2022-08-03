package lombok;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hubin
 * @date 2022年05月24日 10:49
 */
@Slf4j
public class UseDemoTest {

    @Test
    public void test1() {
        // 链式set,需要加@Accessors(chain = true)注解
        UserDemo userDemo = new UserDemo(101)
                .setName("hb")
                .setAddress("hz");

        System.out.println(userDemo.toString());
    }



    @Test
    public void testVarVal() {
        val list = new ArrayList<String>();
        list.add("line1");
        list.add("line2");
        System.out.println(list);


        List<String> list2 = new ArrayList<String>();
        list2.add("line1");
        list2.add("line2");
        System.out.println(list2);


        val str1 = "";

    }


    public void echo(@NonNull String input) {
        System.out.println(input);
    }

    @Test
    public void testNonNull() {
        echo("");
        echo(null);
    }


    @Test
    public void testLog() {
        log.info("test lombok.");
    }


    /**
     * 在没有throws关键词的情况下,隐蔽得抛出受检异常
     */
    @Test
    @SneakyThrows
    public void testSneakyThrows() {
        Thread.sleep(1000);
        System.out.println("end");

    }


}
