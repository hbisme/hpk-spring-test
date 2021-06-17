package vavr;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.UUID;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.Lazy;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Value;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

import static io.vavr.API.$;
import static io.vavr.Predicates.*; // instanceOf
import static io.vavr.API.Case;
import static io.vavr.API.Match;


public class Test1 {

    @Test
    public void test1() {
        Number sum = List.of(1, 2, 3).sum();
        System.out.println(sum.intValue());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -2);
        System.out.println(calendar.getTime());
    }

    /**
     * java List 转 vavr的List
     */
    @Test
    public void test2() {
        java.util.List<String> jList = new ArrayList<>();
        jList.add("a");
        jList.add("b");
        jList.add("c");
        System.out.println(jList);

        // 使用ofAll来转换
        List<String> list = List.ofAll(jList);

        System.out.println(list);
    }


    /**
     * Tuple 比较有用
     */
    @Test
    public void testTuple() {
        Tuple2<String, Integer> tuple2 = Tuple.of("Hello", 100);
        Tuple2<String, Integer> updatedTuple2 = tuple2.map(String::toUpperCase, v -> v * 5);
        String result = updatedTuple2.apply((str, number) -> String.join(", ",
                str, number.toString()));
        System.out.println(result);
    }


    /**
     * 模式匹配
     */
    @Test
    public void whenMatchworks_thenCorrect() {
        int input = 2;
        String output = Match(input).of(
                Case($(1), "one"),
                Case($(2), "two"),
                Case($(3), "three"),
                Case($(), "?"));

        Assert.assertEquals("two", output);
    }

    @Test
    public void testCompose() {
        //使用andThen
        Function1<Integer, Integer> plusOne = a -> a + 1;
        Function1<Integer, Integer> multiplyByTwo = a -> a * 2;
        Function1<Integer, Integer> add1AndMultiplyBy2 = plusOne.andThen(multiplyByTwo);
        Assert.assertEquals(6, add1AndMultiplyBy2.apply(2).intValue());
    }


    @Test
    public void testTry() {
        Try<Integer> computation = Try.of(() -> 1 / 0);

        int errorSentinel = computation.getOrElse(-1);
        Assert.assertEquals(-1, errorSentinel);
    }


    /**
     * 测试Option, 可以去掉很多if/else == null判断 和         if (handlers != null && handlers.size() > 0) { 这样的判断
     */
    @Test
    public void testOption() {
        User user = null;
        // User user = new User(1, "hb");
        // of会构造None或Some对象
        Option<User> mayBe = Option.of(user);
        Option<User> t = mayBe.map(u -> new User(2, u.getName()))
                .filter(u -> u.getId() > 1);
        System.out.println(t.getOrElse(new User(-1, "None")));
    }


    @Test
    public void testLifting() {
        Function2<Integer, Integer, Integer> divide = (a, b) -> a / b;
        Function2<Integer, Integer, Option<Integer>> safeDivide = Function2.lift(divide);

        // = None
        Option<Integer> i1 = safeDivide.apply(1, 0);
        Assert.assertEquals("None", i1.toString());

        // = Some(2)
        Option<Integer> i2 = safeDivide.apply(4, 2);
        Assert.assertEquals(2, i2.get().intValue());
    }


    @Test
    public void testCurried() {
        Function2<Integer, Integer, Integer> sum = (a, b) -> a + b;
        Function1<Integer, Integer> add2 = sum.curried().apply(2);

        Assert.assertEquals(6, add2.apply(4).intValue());
    }


    @Test
    public void group() {
        ArrayList<User> list = new ArrayList();
        list.add(new User(1, "hb1"));
        list.add(new User(1, "hb2"));
        list.add(new User(3, "hb3"));

        Map<Integer, Stream<User>> map = Stream.ofAll(list).groupBy(x -> x.getId());

        System.out.println(map);
    }


    /**
     * Java8的函数式接口最多有两个参数，Vavr最多可有8个参数
     */
    @Test
    public void testFunction() {
        Function3<String, String, String, String> function3 = (a, b, c) -> a + b + c + "hello";
        String result = function3.apply("1", "2", "3");
        System.out.println(result);
    }


    @Test
    public void testMatchCase() {
        int obj = 1;

    }

    /**
     * 数组转map
     */
    @Test
    public void testArrayToMap() {
        Array<String> arr = Array.of("a:1", "b:2", "c:3", "c:4", "e");
        Array<Tuple2<String, String>> tt = arr.map(x -> x.split(":")).filter(x -> x.length == 2)
                .map(x -> {
                    Tuple2<String, String> tuple2 = Tuple.of(x[0], x[1]);
                    return tuple2;
                });

        Map<String, String> m = tt.toMap(x -> x._1, x -> x._2);
        System.out.println(m);

    }

    /**
     * 生成List的数字序列
     */
    @Test
    public void testRange() {
        List<Integer> list = List.range(1, 10);
        System.out.println(list);
    }




    class User {
        private int id;
        private String name;

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    private static Either<String, Integer> computeWithEither(int marks) {
        if (marks < 85) {
            return Either.left("Marks not acceptable");
        } else {
            return Either.right(marks);
        }
    }

}
