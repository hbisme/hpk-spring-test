package priv.hb.sample.tool.vavr;

import org.junit.jupiter.api.Test;

import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import priv.hb.sample.tool.vavr.pojo.User;

/**
 * @author hubin
 * @date 2022年10月13日 10:25
 */
public class ListTest {

    /**
     * 生成List的数字序列
     */
    @Test
    public void testRange() {
        List<Integer> list = List.range(1, 10);
        // 在标准输出中打印,每行一个元素
        list.stdout();
    }


    @Test
    public void test3() {
        // 取交集.使用retainALL
        List<Integer> list1 = List.range(1, 10);
        List<Integer> list2 = List.range(5, 10);
        list1.retainAll(list2).stdout();

        // 内容替换
        System.out.println(list1.replaceAll(1, 11));
    }

    /**
     * 测试peek, 只会输出头元素,不会改变原队列元素
     */
    @Test
    public void testPeek() {
        List<Integer> list1 =
                List.range(1, 10)
                        .map(x -> x + 1)
                        .map(x -> x + 2)
                        .peek(x -> System.out.println(x))
                        .map(x -> x * 2);


        System.out.println(list1);
    }

    @Test
    public void testDebug() {
        List<Integer> list1 =
                List.range(1, 10)
                        .map(x -> {
                            int y = x + 1;
                            return y;
                        })
                        .map(x -> x + 1)
                        .map(x -> x * 2);

        System.out.println(list1);
    }


    /**
     * 使用sliding滑动窗口,将一个集合中的数据,按数据量来拆分成多个List.
     */
    @Test
    public void test2() {
        List<Integer> range = List.range(0, 100);
        System.out.println(range.size());
        java.util.List<List<Integer>> sliding = range.sliding(30, 30).toJavaList();
        System.out.println(sliding);
    }


    @Test
    public void group() {
        final List<User> of = List.of(
                new User(1, "hb1"),
                new User(1, "hb11"),
                new User(2, "hb2"),
                new User(3, "hb3"));

        Map<Integer, List<User>> tuple2s = of.groupBy(x -> x.getId());
        System.out.println(tuple2s);
    }


    @Test
    public void split() {
        List<User> of = List.of(
                new User(1, "hb1"),
                new User(2, "hb2"),
                new User(3, "hb3"),
                new User(4, "hb4"),
                new User(5, "hb5")
        );

        Iterator<List<User>> grouped = of.grouped(2);
        System.out.println(grouped.toJavaList());


    }


}
