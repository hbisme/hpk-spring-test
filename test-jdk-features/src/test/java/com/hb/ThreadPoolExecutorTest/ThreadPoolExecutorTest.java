package com.hb.ThreadPoolExecutorTest;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolExecutorTest {

    public static volatile int count = 0;

    public static synchronized void addCount() {
        count++;
    }

    public static synchronized void delCount() {
        count--;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        int corePoolSize = 4;
        int maximumPoolSize = 4;
        long keepAliveTime = 10;
        TimeUnit unit = TimeUnit.MINUTES;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(200);
        ThreadFactory threadFactory = new NameTreadFactory();
        RejectedExecutionHandler handler = new MyIgnorePolicy();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        // 预启动所有核心线程
        executor.prestartAllCoreThreads();

        for (int i = 0; i < 10; i++) {

            if (count < 2) {
                MyTask task = new MyTask(String.valueOf(i));
                executor.execute(task);
            }
            Thread.sleep(10000);
        }

        System.in.read();

    }


    static class NameTreadFactory implements ThreadFactory {
        private final AtomicInteger mThreadNum = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "my-thread-" + mThreadNum.getAndIncrement());
            System.out.println(t.getName() + " has been created");
            return t;
        }
    }

    public static class MyIgnorePolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            doLog(r, executor);
        }

        private void doLog(Runnable r, ThreadPoolExecutor e) {
            System.err.println(r.toString() + " rejected");
        }
    }

    static class MyTask implements Runnable {
        private String name;

        public MyTask(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }


        @Override
        public void run() {
            addCount();

            System.out.println(this.toString() + " is running!, count: " + count);
            try {
                Thread.sleep(10000);
                delCount();
                System.out.println(this.toString() + "is ending!, count: " + count);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
            }
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "MyTask{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }


}
