package priv.hb.sample.ThreadPoolExecutorTest;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

/**
 * @author hubin
 * @date 2022年11月07日 10:50
 */
public class PoolExceptionTest {

    @SneakyThrows
    @Test
    public void test1() {
        //创建一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        //当线程池抛出异常后 submit无提示，其他线程继续执行
        Future<?> submit = executorService.submit(new task());
        // 不使用get()方法,submit不会抛出异常
        // submit.get();

        // 当线程池抛出异常后 execute抛出异常，其他线程继续执行新任务
        executorService.execute(new task());
    }

    @SneakyThrows
    @Test
    public void test2() {
        //1.实现一个自己的线程池工厂
        ThreadFactory factory = (Runnable r) -> {
            //创建一个线程
            Thread t = new Thread(r);
            //给创建的线程设置UncaughtExceptionHandler对象 里面实现异常的默认逻辑
            t.setDefaultUncaughtExceptionHandler((Thread thread1, Throwable e) -> {
                System.out.println("线程工厂设置的exceptionHandler" + e.getMessage());
            });
            return t;
        };

        //2.创建一个自己定义的线程池，使用自己定义的线程工厂
        ExecutorService executorService = new ThreadPoolExecutor(
                1,
                1,
                0,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue(10),
                factory);

        // submit无提示
        executorService.submit(new task());

        Thread.sleep(1000);
        System.out.println("==================为检验打印结果，1秒后执行execute方法");

        // execute 方法被线程工厂factory 的UncaughtExceptionHandler捕捉到异常
        executorService.execute(new task());


    }


    //任务类
    class task implements Runnable {

        @Override
        public void run() {
            System.out.println("进入了task方法！！！");
            int i = 1 / 0;

        }
    }
}
