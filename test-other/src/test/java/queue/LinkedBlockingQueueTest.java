package queue;

import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueTest {

    @Test
    public void test1() throws InterruptedException {
        LinkedBlockingQueue<String> list = new LinkedBlockingQueue(10);
        list.put("a");
        list.put("b");
        list.put("c");

        System.out.println(list);
        System.out.println(list.size());

        list.clear();
        list.put("d");
        System.out.println(list.size());

        list.clear();
        System.out.println(list.size());





    }


}
