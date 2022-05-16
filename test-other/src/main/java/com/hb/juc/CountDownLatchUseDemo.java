package com.hb.juc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *    跑步比赛,裁判员和运动员是不同的线程.
 *    假设有 3 个运动员参与比赛，裁判员需要等待所有运动员准备完成后,才能发令开始比赛.
 *    同时运动员要等裁判发令后,才能跑向终点.
 * @author hubin
 * @date 2022年05月12日 5:56 下午
 */
public class CountDownLatchUseDemo {

    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(3);
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        final CountDownLatch startDownLatch = new CountDownLatch(1);


        for (int i = 0; i < 3; i++) {
            executorService.execute(() -> {
                System.out.println("运动员" + Thread.currentThread().getName() + "已经准备好了");
                countDownLatch.countDown();
                try {
                    startDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("运动员" + Thread.currentThread().getName() + "跑向终点");



            });
        }

        countDownLatch.await();
        System.out.println("裁判发令,运动员可以开始跑");
        startDownLatch.countDown();

    }
}
