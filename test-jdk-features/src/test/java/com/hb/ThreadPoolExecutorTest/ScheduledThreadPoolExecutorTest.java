package com.hb.ThreadPoolExecutorTest;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 *
 * java自带的定时间隔任务调度测试.
 * ScheduledThreadPoolExecutor 是一个使用线程池执行定时任务的类.采用多线程来执行任务.
 * 策略分两大类
 * 1. 在第一延迟后只执行一次某个任务;
 * 2. 在一定延迟之后周期性得执行某个任务.
 */
public class ScheduledThreadPoolExecutorTest {
    private ScheduledThreadPoolExecutor executor;
    private Runnable task;

    private ScheduledThreadPoolExecutor initExecutor() {
        return new ScheduledThreadPoolExecutor(2);
    }

    private Runnable initTask() {
        long start = System.currentTimeMillis();
        return () -> {
            System.out.println("start task: " + getPeriod(start, System.currentTimeMillis()));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("end task: " + getPeriod(start, System.currentTimeMillis()));
        };
    }

    @Before
    public void before() {
        executor = initExecutor();


        task = initTask();
    }

    /**
     * 每次任务执行的开始时间是上次任务执行开始时间,加上间隔时间.
     *
     * @throws InterruptedException
     */
    @Test
    public void testFixedTask() throws InterruptedException {
        System.out.println("start main thread");
        executor.scheduleAtFixedRate(task, 15, 30, TimeUnit.SECONDS);
        Thread.sleep(120000);
        System.out.println("end main thread");
    }

    /**
     * 每次任务执行的开始时间是上次任务执行完成时间,加上间隔时间.
     *
     * @throws InterruptedException
     */
    @Test
    public void testDelayedTask() throws InterruptedException {
        System.out.println("start main thread");
        executor.scheduleWithFixedDelay(task, 15, 30, TimeUnit.SECONDS);
        Thread.sleep(120000);
        System.out.println("end main thread");
    }

    /**
     * 用定时调度执行一次性任务.
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException {
        System.out.println("start main thread");
        executor.schedule(task, 30, TimeUnit.SECONDS);

        Thread.sleep(120000);
        System.out.println("end main thread");
    }

    /**
     * 测试停止定时调度器
     * @throws InterruptedException
     */
    @Test
    public void testStopSchedule() throws InterruptedException {
        System.out.println("start main thread");
        executor.scheduleAtFixedRate(task, 0, 10, TimeUnit.SECONDS);
        Thread.sleep(3000);
        executor.shutdownNow();
        System.out.println("end main thread");
    }



    /**
     * 测试 调度器 并行情况
     * @throws InterruptedException
     */
    @Test
    public void testParallel() throws InterruptedException {
        ScheduledThreadPoolExecutor  executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleWithFixedDelay(() -> {
            System.out.println("start: " + new Date());
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0,10, TimeUnit.SECONDS);

        Thread.sleep(30000000);

    }


    private int getPeriod(long start, long end) {
        return (int) (end - start) / 1000;
    }


}
