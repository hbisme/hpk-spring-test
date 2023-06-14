package priv.hb.sample.tool.vavr;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import cn.hutool.core.lang.Editor;
import cn.hutool.core.lang.copier.Copier;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedFunction1;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;


/**
 * future 并发函数,有异常处理,有返回值
 *
 * @author hubin
 * @date 2022年11月07日 11:34
 */
public class FutureTest {
    @Test
    public void testFutureOnSuccess() {
        String word = "hello world";
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<String> future = Future
                .of(executor, () -> word);

        future
                .onFailure(throwable -> Assert.fail("不应该走到failure分支"))
                .onSuccess(result -> System.out.println(result));
    }

    @Test
    public void testFutureOnFailure() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future
                .of(executor, () -> {
                    throw new RuntimeException();
                })
                .onFailure(throwable -> System.out.println("正处理异常" + throwable))
                .onSuccess(result -> System.out.println(result))
        ;
    }

    /**
     * 模拟一个List中有10个异步对象, 哪个先执行好count就+1
     */
    @Test
    public void multipleAction() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<CheckedFunction0<Integer>> checkedFunction0List = List.range(0, 10).map(x -> {
            int random = new Random().nextInt(1000);

            CheckedFunction0<Integer> integerCopier = () -> {
                try {
                    Thread.sleep(random);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return random;
            };
            return integerCopier;
        });


        List<Future<Integer>> futureList = checkedFunction0List.map(x -> {
            Future<Integer> of = Future.of(executor, x);
            return of;
        });

        // 声明一个 AtomicInteger 类型的计数器变量
        // 这里不能用Integer count = 0,因为多线程修改会不安全!
        AtomicInteger count = new AtomicInteger(0);

        Integer countMax = futureList.size();


        futureList.forEach(x -> {
            x.onSuccess(y -> {
                count.incrementAndGet();
                System.out.println("count: " + count.toString() + ",sleepTime:" + y.toString() + ",countMax: " + countMax);
            });
        });

        Thread.sleep(10000);


    }

    @Test
    public void test0() {

        Future<Integer> future1 = Future.of(() -> {
            // 模拟一个耗时的计算
            Thread.sleep(1000);
            return 42;
        });

        Future<Integer> future2 = Future.of(() -> {
            // 模拟另一个耗时的计算
            Thread.sleep(500);
            return 10;
        });

        Future<Integer> future3 = future1.flatMap(result1 ->
                future2.map(result2 -> result1 + result2));

        System.out.println("正在执行计算...");

        future3.onComplete(result -> {
            if (result.isSuccess()) {
                System.out.println("计算结果为：" + result.get());
            } else {
                System.out.println("计算失败：" + result.getCause());
            }
        });

        System.out.println("继续执行其他操作...");

    }


    /**
     * 没有异常处理,会抛出异常
     */
    @Test
    public void test1() {
        CheckedFunction0<Integer> intFunc = () -> {
            Thread.sleep(3000);

            System.out.println(1 / 0);
            return 2;
        };

        Future<Integer> of = Future.of(intFunc);

        // 转换成try就会阻塞,知道得到结果(get())
        Try<Integer> toTry = of.toTry();

        System.out.println(123);
        System.out.println(toTry);
        System.out.println(toTry.getCause());
    }


    /**
     * 有异常处理,会抛出返回值.
     */
    @Test
    public void test2() {
        CheckedFunction0<Integer> intFunc = () -> {
            Thread.sleep(3000);

            System.out.println(1 / 0);
            return 2;
        };

        Future<Integer> of = Future.of(intFunc);

        // 转换成try就会阻塞,知道得到结果(get())
        Try<Integer> toTry = of.toTry();
        Try<Integer> recover = toTry.recover(ArithmeticException.class, -1);


        System.out.println(recover);


    }
}
