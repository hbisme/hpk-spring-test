package ThreadPoolExecutor;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 如果向Executor提交了一组计算任务，并且希望在计算完成后获得结果，那么可以保留与每个任务关联的Future，然后反复使用get方法，
 * 同时将参数timeout指定为0，从而通过轮询来判断任务是否完成。这种方法虽然可行，但却有些繁琐。
 * 幸运的是，还有一种更好的方法：完成服务CompletionService。
 */
public class ExecutorCompletionServiceTest {

    @Test
    public void test() throws InterruptedException, ExecutionException {

        Executor executor = Executors.newFixedThreadPool(3);
        ExecutorCompletionService<String> service = new ExecutorCompletionService<>(executor);
        for (int i = 0; i < 5; i++) {
            int seqNo = i;
            service.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    int sleeptime = new Random(1).nextInt(3000);
                    Thread.sleep(sleeptime);
                    return "HelloWorld-" + seqNo + "-" + Thread.currentThread().getName();
                }
            });
        }

        for (int j = 0; j < 5; j++) {
            System.out.println(service.take().get());
        }

    }
}
