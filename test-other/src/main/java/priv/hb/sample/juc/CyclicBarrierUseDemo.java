package priv.hb.sample.juc;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * 场景: 跑步远动员比赛,当所有运动员(线程)都到达终点后,比赛才结束,远动员才能退场.
 * cyclicBarrier控制
 *
 * @author hubin
 * @date 2022年05月13日 11:23 上午
 */
public class CyclicBarrierUseDemo {
    public static void main(String[] args) {
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> {
            System.out.println("所有运动员都已达到终点,比赛结束");
        });

        final ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            executorService.execute(() -> {

                System.out.println("运动员" + Thread.currentThread().getName() + "已经到终点");
                try {
                    cyclicBarrier.await();
                    System.out.println("运动员" + Thread.currentThread().getName() + "退场");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }

        System.out.println("观众正在观看比赛");

    }
}
