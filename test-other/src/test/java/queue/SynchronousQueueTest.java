package queue;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.SynchronousQueue;

/**
 * 同步队列测试
 * 这是一个很有意思的阻塞队列，其中每个插入操作必须等待另一个线程的移除操作，同样任何一个移除操作都等待另一个线程的插入操作。
 * 因此此队列内部其 实没有任何一个元素，或者说容量是0，严格说并不是一种容器。
 * 由于队列没有容量，因此不能调用peek操作，因为只有移除元素时才有元素。
 */
public class SynchronousQueueTest {
    @Test
    public void test() throws InterruptedException {
        SynchronousQueue<Integer> queue = new SynchronousQueue<Integer>();

        new Product(queue).start();
        new Customer(queue).start();

        Thread.sleep(10000);

    }

    static class Product extends Thread {
        SynchronousQueue<Integer> queue;

        public Product(SynchronousQueue<Integer> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                int rand = new Random().nextInt(1000);
                try {
                    Thread.sleep(1000);
                    System.out.println("生产了一个产品: " + rand);
                    System.out.println("等待三秒后运送出去...");
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    queue.put(rand);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(queue.isEmpty());
            }
        }
    }

    static class Customer extends Thread {
        SynchronousQueue<Integer> queue;

        public Customer(SynchronousQueue<Integer> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println("消费了一个产品: " + queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("----------");

            }
        }
    }
}
