package com.hb.vavr;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;

import io.vavr.Function0;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.Function4;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.val;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;


/**
 * 基础测试
 */
public class BaseTest1 {

    @Test
    public void test1() {
        Number sum = List.of(1, 2, 3).sum();
        System.out.println(sum.intValue());
    }

    /**
     * java List 转 vavr的List
     */
    @Test
    public void test2() {
        val jList = new ArrayList<String>();
        jList.add("a");
        jList.add("b");
        jList.add("c");
        System.out.println(jList);

        // 使用ofAll来转换
        val list = List.ofAll(jList);

        System.out.println(list);
    }


    /**
     * Tuple 比较有用
     */
    @Test
    public void testTuple() {
        Tuple2<String, Integer> tuple2 = Tuple.of("Hello", 100);
        // map方法返回的类型同原来的Tuple类型相同
        Tuple2<String, Integer> updatedTuple2 = tuple2.map(String::toUpperCase, v -> v * 5);
        // apply方法返回的类型可以是任意的
        String result = updatedTuple2.apply(
                (str, number) -> String.join(", ", str, number.toString()));
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


    /**
     * 测试Option, 可以去掉很多if/else == null判断 和         if (handlers != null && handlers.size() > 0) { 这样的判断
     */
    @Test
    public void testOption() {
        User user = null;
        // User user = new User(1, "hb");
        // of会构造None或Some对象
        Option<User> mayBe = Option.of(user);
        Option<User> t = mayBe
                .map(u -> new User(2, u.getName()))
                .filter(u -> u.getId() > 1);
        System.out.println(t.getOrElse(new User(-1, "None")));
    }

    @Test
    public void group() {
        ArrayList<User> list = new ArrayList();
        list.add(new User(1, "hb1"));
        list.add(new User(1, "hb2"));
        list.add(new User(3, "hb3"));

        final List<User> of = List.of(
                new User(1, "hb1"),
                new User(2, "hb2"),
                new User(3, "hb3"));

        val tuple2s = of.groupBy(x -> x.getId());
        System.out.println(tuple2s);
    }


    /**
     * Java8的函数式接口最多有两个参数，Vavr最多可有8个参数
     */
    @Test
    public void testFunction() {
        Function3<String, String, String, String> function3 = (a, b, c) -> a + b + c + "hello";
        val result = function3.apply("1", "2", "3");
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


    /**
     * 测试函数组合(compose)
     */
    @Test
    public void testComposition() {
        Function1<Integer, Integer> plusOne = a -> a + 1;
        Function1<Integer, Integer> multiplyByTwo = a -> a * 2;
        // 通过组合两个函数生成一个新的函数,这个函数就能复用
        Function1<Integer, Integer> add1AndMultiplyBy2 = plusOne.andThen(multiplyByTwo);

        // 调用组合函数
        add1AndMultiplyBy2.apply(2);

        System.out.println(List.of(1, 2, 3).map(plusOne).map(multiplyByTwo));
        // List中应用组合函数
        System.out.println(List.of(1, 2, 3).map(add1AndMultiplyBy2));

        // 算子是右结合的,等同于上面的 "add1AndMultiplyBy2"
        Function1<Integer, Integer> add1AndMultiplyBy2_another = multiplyByTwo.compose(plusOne);
        System.out.println(List.of(1, 2, 3).map(add1AndMultiplyBy2_another));

    }

    /**
     * 测试提升函数(Lifting)
     * <p>
     * divide函数是一个只接受非零因子的部分函数
     * 使用lift将divide转化为一个定义了所有输入的总函数。
     * 1. 如果使用不允许的输入值调用函数，则提升的函数将返回None而不是引发异常。
     * 2. 如果使用允许的输入值调用函数，则提升的函数将返回Some。
     */
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
    public void testListing2() {
        Function2<Integer, Integer, Integer> partialFunction = (x, y) -> {
            if (x < 0 || y < 0) {
                throw new RuntimeException("输入参数不合法");
            }
            return x + y;
        };

        Function2<Integer, Integer, Option<Integer>> liftFunction = Function2.lift(partialFunction);
        Option<Integer> optionResult = liftFunction.apply(1, -2);
        System.out.println(optionResult);
    }

    /**
     * 测试部分应用函数
     */
    @Test
    public void testPartitionFunction() {
        Function2<Integer, Integer, Integer> sum = (a, b) -> a + b;
        // add2就是部分应用函数, 第一个参数a固定为值2。
        Function1<Integer, Integer> add2 = sum.apply(2);

        Integer r1 = add2.apply(4);
        System.out.println(r1);
    }


    /**
     * 测试柯里化
     */
    @Test
    public void testCurried() {
        Function2<Integer, Integer, Integer> sum = (a, b) -> a + b;
        // .curried()之外，此代码与部分应用函数中给出的参数示例相同
        Function1<Integer, Integer> add2 = sum
                .curried()
                .apply(2);

        Assert.assertEquals(6, add2.apply(4).intValue());
    }

    /**
     * Function2两个参数时,curried看起来和部分应用函数相同,但是从Function3开始就不同了
     * curried,都会返还Function1,简单来说就是将Function3转换成Function1的组合
     */
    @Test
    public void testCurried2() {
        Function3<Integer, Integer, Integer, Integer> sum = (a, b, c) -> (a + b) * c;
        Function1<Integer, Function1<Integer, Integer>> add2 = sum.curried().apply(2);
        System.out.println(add2.apply(4).apply(3));
    }


    @Test
    public void testCurried3() {
        Function4<Integer, Integer, Integer, Integer, Integer> sum = (a, b, c, d) -> (a + b) * c + d;
        Function1<Integer, Function1<Integer, Function1<Integer, Integer>>> add2 = sum.curried().apply(2);
        System.out.println(add2.apply(4).apply(3).apply(1));
    }

    /**
     * 测试"记忆化"
     */
    @Test
    public void TestMemoization() {
        Function0<Double> hashCache = Function0.of(() -> Math.random()).memoized();
        double randomValue1 = hashCache.apply();
        double randomValue2 = hashCache.apply();
        // 因为memoized, 两个random出来的值是相同的
        System.out.println(randomValue1);
        System.out.println(randomValue2);
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
