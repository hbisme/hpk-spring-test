package com.hb.controller;

import com.hb.eventListener.listener.netty.listener.MasterResponseListener;
import com.hb.eventListener.listener.netty.master.MasterHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RestController
public class FutureController {

    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor executor;

    /**
     * 如果返回的是Future<String>, web返回
     * <p>
     * {
     * "cancelled": false,
     * "done": true
     * }
     * <p>
     * 而不是想要的结果
     *
     * @return
     */
    @GetMapping("/future1")
    public Future<String> test1() {

        Future<String> f = executor.submit(() -> {
            System.out.println("in executor future call");
            return "ok";
        });
        return f;
    }

    @GetMapping("/future2")
    public Future<String> test2() {
        CountDownLatch latch = new CountDownLatch(1);

        MasterResponseListener responseListener = new MasterResponseListener("request", false, latch, null);
        (new MasterHandler()).addListener(responseListener);

        Future<String> f = executor.submit(() -> {
            latch.await(1, TimeUnit.HOURS);
            if (!responseListener.getReceiveResult()) {
                System.out.println("任务({})信号丢失，3小时未收到work返回：{}");
            }
            return responseListener.getResponse();
        });


        System.out.println("发送消息到外部");

        return f;
    }

}
