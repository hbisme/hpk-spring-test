package com.hb.controller;

import com.hb.service.WebAsyncService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.Arrays;

/**
 * Spring Boot 提供的 WebAsyncTask的异步API的测试.
 */
@RestController
public class WebAsyncController {
    private final WebAsyncService webAsyncService;
    private final static String ERROR_MESSAGE = "Task error";
    private final static String TIME_MESSAGE = "Task timeout";

    @Autowired
    public WebAsyncController(WebAsyncService asyncService) {
        this.webAsyncService = asyncService;
    }

    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor executor;


    /**
     * WebAsyncTask的正常任务的测试
     *
     * @return
     */
    @GetMapping("/completion")
    public WebAsyncTask<String> asyncTaskCompletion() {
        System.out.println("请求处理线程: " + Thread.currentThread().getName());

        // 模拟开启一个异步任务, 超时时间为10s
        WebAsyncTask<String> asyncTask = new WebAsyncTask<>(10 * 1000L, () -> {
            System.out.println("异步工作线程: " + Thread.currentThread().getName());
            // 任务处理时间为5s, 不超时
            Thread.sleep(5 * 1000L);
            return webAsyncService.generateUUID();
        });

        // 任务执行完成时调用该方法.
        asyncTask.onCompletion(() -> System.out.println("异步任务执行完成"));
        System.out.println("继续处理其他事情");
        return asyncTask;
    }

    @GetMapping("/multipleCompletion")
    public String multipleAsyncTaskCompletion(String idStr) throws InterruptedException {
        System.out.println("请求处理线程: " + Thread.currentThread().getName());

        String[] ids = idStr.split(",");

        Arrays.stream(ids).parallel().forEach(id -> {
            System.out.println("异步工作线程: " + Thread.currentThread().getName());
            // 任务处理时间为5s, 不超时
            try {
                Thread.sleep(5 * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Thread.sleep(10000);
        return "ok";
    }

    /**
     * WebAsyncTask 执行任务返回异常的测试
     *
     * @return
     */
    @GetMapping("/exception")
    public WebAsyncTask<String> assyncTaskException() {
        System.out.println("请求处理线程: " + Thread.currentThread().getName());

        // 模拟开启一个异步任务, 超时时间为10s
        WebAsyncTask<String> asyncTask = new WebAsyncTask<>(10 * 1000L, () -> {
            System.out.println("异步工作线程: " + Thread.currentThread().getName());
            // 任务处理时间为5s, 不超时
            Thread.sleep(5 * 1000L);
            throw new Exception(ERROR_MESSAGE);
        });
        // 任务执行完成时调用该方法.
        asyncTask.onCompletion(() -> System.out.println("异步任务执行完成"));

        asyncTask.onError(() -> {
                    System.out.println("任务执行异常");
                    return ERROR_MESSAGE;
                }
        );
        System.out.println("继续处理其他事情");
        return asyncTask;
    }

    /**
     * WebAsyncTask 执行任务返回超时的测试
     *
     * @return
     */
    @GetMapping("/timeout")
    public WebAsyncTask<String> asycTaskTimeout() {
        System.out.println("请求处理线程: " + Thread.currentThread().getName());

        // 模拟开启一个异步任务, 超时时间为10s
        WebAsyncTask<String> asyncTask = new WebAsyncTask<>(10 * 1000L, () -> {
            System.out.println("异步工作线程: " + Thread.currentThread().getName());
            // 任务处理时间为15s, 超时
            Thread.sleep(15 * 1000L);
            return "TIME_MESSAGE";
        });

        // 任务执行完成时调用该方法.
        asyncTask.onCompletion(() -> System.out.println("异步任务执行完成"));

        asyncTask.onTimeout(() -> {
            System.out.println("任务执行超时");
            return TIME_MESSAGE;
        });

        System.out.println("继续处理其他事情");
        return asyncTask;
    }


    /**
     * WebAsyncTask 任务线程使用线程池的例子
     *
     * @return
     */
    @GetMapping("/threadPool")
    public WebAsyncTask<String> asyncTaskThreadPool() {
        return new WebAsyncTask<String>(10 * 1000L, executor,
                () -> {
                    System.out.println("异步工作线程: " + Thread.currentThread().getName());
                    return webAsyncService.generateUUID();
                });
    }

}



