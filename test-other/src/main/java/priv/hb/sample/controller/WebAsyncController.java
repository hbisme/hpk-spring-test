package priv.hb.sample.controller;

import priv.hb.sample.service.WebAsyncService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final static String TASK_TIMEOUT = "Task timeout";

    @Autowired
    public WebAsyncController(WebAsyncService asyncService) {
        this.webAsyncService = asyncService;
    }

    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor executor;


    /**
     * WebAsyncTask的正常任务的测试
     * web请求后,异步结果WebAsyncTask<String> 不会立刻返回结果,
     * 浏览器将会卡住,等异步有结果后,浏览器才会有结果.
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
    public String multipleAsyncTaskCompletion(@RequestParam String idStr) throws InterruptedException {
        System.out.println("请求处理线程: " + Thread.currentThread().getName());

        String[] ids = idStr.split(",");

        Arrays.stream(ids).parallel().forEach(id -> {
            System.out.println("工作处理线程: " + Thread.currentThread().getName());
            try {
                Thread.sleep(5 * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        return "ok";
    }

    /**
     * WebAsyncTask 执行任务返回异常的测试
     * web会卡住,等异步返回异常结果后显示结果.
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
        // 任务执行完成时调用该方法,如果中间有异常,会在onError()后调用
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


        asyncTask.onTimeout(() -> {
            System.out.println("任务执行超时");
            return TASK_TIMEOUT;
        });

        // 任务执行完成时调用该方法.
        asyncTask.onCompletion(() -> System.out.println("异步任务执行完成"));

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
                    Thread.sleep(5000);
                    String threadName = "异步工作线程: " + Thread.currentThread().getName() + " ";
                    System.out.println(threadName);
                    return threadName + webAsyncService.generateUUID();
                });
    }


    /**
     * WebAsyncTask 测试前端快速返回后,后端异步任务还会不会执行
     * 结论是 如果返回的类型不是WebAsyncTask,则不会执行WebAsyncTask中的任务,
     * 即 一行 "在异步线程中打印:" 的日志都不会打印.
     */
    @GetMapping("/returnQuick")
    public boolean asycTaskNotWork() throws InterruptedException {
        System.out.println("请求处理线程: " + Thread.currentThread().getName());

        // 模拟开启一个异步任务, 超时时间为10s
        WebAsyncTask<String> asyncTask = new WebAsyncTask<>(10 * 1000L, () -> {

            int i = 0;
            System.out.println("异步工作线程: " + Thread.currentThread().getName());
            // 任务处理时间为15s, 超时

            while (i < 10) {
                System.out.println("在异步线程中打印:" + i);
                Thread.sleep(1000);
                i++;
            }
            return "TIME_MESSAGE";
        });

        // 任务执行完成时调用该方法.
        asyncTask.onCompletion(() -> System.out.println("异步任务执行完成"));

        asyncTask.onTimeout(() -> {
            System.out.println("任务执行超时");
            return TASK_TIMEOUT;
        });

        System.out.println("继续处理其他事情");
        Thread.sleep(2000);
        return true;
    }

}



