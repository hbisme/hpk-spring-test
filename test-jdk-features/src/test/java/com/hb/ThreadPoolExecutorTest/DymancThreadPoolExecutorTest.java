package com.hb.ThreadPoolExecutorTest;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 测试 线程池动态调整 最大线程数量(是可行的)
 */
public class DymancThreadPoolExecutorTest {
    public static void main(String[] args) throws InterruptedException {
        int corePoolSize = 4;
        int maximumPoolSize = 4;
        long keepAliveTime = 10;
        TimeUnit unit = TimeUnit.MINUTES;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(200);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);

        Runnable task = new ThreadPoolExecutorTest.MyTask(String.valueOf(-1));
        executor.execute(task);

        executor.setCorePoolSize(10);
        executor.setMaximumPoolSize(10);

        for (int i = 0; i < 20; i++) {
            Runnable task2 = new ThreadPoolExecutorTest.MyTask(String.valueOf(i));
            executor.execute(task2);
        }

        Thread.sleep(1000);
        executor.setCorePoolSize(1);
        executor.setMaximumPoolSize(1);
        for (int i = 100; i < 110; i++) {
            Runnable task2 = new ThreadPoolExecutorTest.MyTask(String.valueOf(i));
            executor.execute(task2);
        }


    }
}
