package priv.hb.sample.ThreadPoolExecutor;

import com.alibaba.fastjson.JSON;

import priv.hb.sample.queue.JobElement;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;


public class PriorityBlockingQueueTest {
    PriorityBlockingQueue<JobElement> queue;

    @Before
    public void before() {
        queue = new PriorityBlockingQueue<JobElement>(10000, Comparator.comparing(JobElement::getPriorityLevel));
        for (int i = 0; i < 12; i++) {
            JobElement jobElement = new JobElement();
            Random rand = new Random();
            int value = rand.nextInt() % 10 * 100;
            jobElement.setJobId(String.valueOf(i));
            jobElement.setPriorityLevel(value);
            queue.put(jobElement);
        }
    }

    @Test
    public void doTest1() throws InterruptedException {
        for (int i = 0; i < 11; i++) {
            JobElement jobElement = queue.take();
            System.out.println(jobElement);
        }

        System.out.println(queue.size());
    }

    @Test
    public void doTest2() throws InterruptedException {
        System.out.println(queue.size());

        // PriorityBlockingQueue<JobElement> arr = queue;

        ArrayList<JobElement> list = new ArrayList<JobElement>();

        for (JobElement jobElement : queue) {
            JobElement job = queue.take();
            list.add(job);
        }


        String str = JSON.toJSONString(list);
        System.out.println(str);


        System.out.println(queue.size());
        //
        System.out.println("----------");
        // doTest1();

    }


}
