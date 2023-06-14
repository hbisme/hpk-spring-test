package priv.hb.sample.thread;

import org.junit.jupiter.api.Test;

import io.vavr.CheckedFunction0;
import io.vavr.CheckedRunnable;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import lombok.SneakyThrows;

/**
 * 两个线程分别使用ThreadLocal当线程内的计数器,并不会互相覆盖各自的计数器.
 * Thread的常见应用场景:
 *     代替参数显式传递(很少使用)
 *     存储全局用户登录信息
 *     存储数据库连接，以及Session等信息
 *     Spring事务处理方案
 *
 * @author hubin
 * @date 2022年11月08日 10:17
 */
public class ThreadLocalTest {
    private static ThreadLocal<Integer> localVar = new ThreadLocal<>();

    void print(ThreadLocal<Integer> localVar) {
        System.out.println("thread: " + Thread.currentThread().getName() + " localVar: " + localVar.get());
        localVar.set(localVar.get() + 1);
    }

    @SneakyThrows
    @Test
    public void test1() {

        Runnable func1 = () -> {
            localVar.set(1);
            List.range(0, 10).forEach(x -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                print(localVar);
            });

        };

        Runnable func2 = () -> {
            localVar.set(1);
            List.range(0, 10).forEach(x -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                print(localVar);
            });
        };

        new Thread(func1).start();
        new Thread(func2).start();



        Thread.sleep(100000);
    }

}
