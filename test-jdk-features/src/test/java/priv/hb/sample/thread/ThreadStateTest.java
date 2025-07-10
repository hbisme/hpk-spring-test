package priv.hb.sample.thread;

import java.util.Scanner;

import org.junit.Test;

import lombok.SneakyThrows;

/**
 * JAVA 线程状态测试
 *
 * @author hubin
 * @date 2023年10月10日 13:53
 */
public class ThreadStateTest {
    /**
     * 模拟cpu运行
     *
     * @param duration
     */
    public static void cpuRun(long duration) {

        final long startTime = System.currentTimeMillis();
        int num = 0;
        while (true) {
            num++;
            if (num == Integer.MAX_VALUE) {
                System.out.println(Thread.currentThread() + "rest");
                num = 0;
            }
            if (System.currentTimeMillis() - startTime > duration) {
                return;
            }
        }
    }


    public static void NEW() {
        Thread t = new Thread();
        System.out.println(t.getState());
    }

    @Test
    public void testNewState() {
        Thread t = new Thread();
        System.out.println(t.getState());
    }


    @Test
    public void testRunnableState() throws InterruptedException {
        Thread t = new Thread() {

            @Override
            public void run() {
                cpuRun(20000);
            }

        };

        t.start();
        System.out.println(t.getState());
        t.join();
    }


    /**
     * 当线程等待io的时候是什么状态?
     * 在io 阻塞读的时候线程状态也是runnable的。
     *
     * @throws InterruptedException
     */
    @Test
    public void testRunnableInBlockedIO() throws InterruptedException {
        Scanner in = new Scanner(System.in);
        Thread t1 = new Thread("demo-t1") {

            @Override
            public void run() {
                try {
                    System.out.println("start io read---------");
                    // 命令行中的阻塞读
                    String input = in.nextLine();
                    System.out.println(input);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    in.close();
                }
            }
        };
        t1.start();
        System.out.println(t1.getState());

        t1.join();
    }


    /**
     * 这个状态通常是线程争抢锁，被block住了。
     *
     * @throws InterruptedException
     */
    @Test
    public void testBLOCKEDState() throws InterruptedException {
        final Object lock = new Object();

        Runnable run = new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < Integer.MAX_VALUE; i++) {

                    synchronized (lock) {
                        cpuRun(500);
                        System.out.println(i);
                    }

                }
            }
        };

        Thread t1 = new Thread(run);
        t1.setName("t1");
        Thread t2 = new Thread(run);
        t2.setName("t2");

        t1.start();
        t2.start();

        Thread.sleep(100);
        System.out.println(t1.getState());
        System.out.println(t2.getState());

    }


    /**
     * 通过debug里的Threads来查看线程状态变化
     */
    @SneakyThrows
    @Test
    public void testWAITINGState() {
        final Object lock = new Object();
        Thread t1 = new Thread() {

            @Override
            public void run() {

                int i = 0;

                while (true) {
                    synchronized (lock) {
                        System.out.println("t1 running");
                        cpuRun(4000);
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                        }
                        System.out.println(i++);
                        System.out.println("t1 end");

                    }
                }
            }
        };

        Thread t2 = new Thread() {

            @Override
            public void run() {
                while (true) {
                    synchronized (lock) {
                        System.out.println("t2 running");
                        cpuRun(10000);
                        lock.notifyAll();
                        System.out.println("t2 end");
                    }
                    //这里需要一定时间执行，否则t1 可能一直抢不到锁
                    cpuRun(100);
                }
            }
        };

        t1.setName("^^t1^^");
        t2.setName("^^t2^^");

        t1.start();
        t2.start();


        Thread.sleep(50000);
    }

}
