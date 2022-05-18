package queue;

import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class LinkedBlockingQueueTest {

    @Test
    public void test1() throws InterruptedException {
        BlockingQueue<String> list = new LinkedBlockingQueue(10);

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


    @Test
    public void test2() throws InterruptedException {
       BlockingQueue<String> list = new LinkedBlockingQueue(10);
        list.put("a");
        list.put("b");
        list.put("c");

        System.out.println(list);

        Object[] arr = list.toArray();
        
        list.clear();
        System.out.println(arr.length);
        System.out.println(list);
    }
}
