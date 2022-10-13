package com.hb.guava.retry;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;


import java.util.concurrent.TimeUnit;

import io.github.itning.retry.Attempt;
import io.github.itning.retry.Retryer;
import io.github.itning.retry.RetryerBuilder;
import io.github.itning.retry.listener.RetryListener;
import io.github.itning.retry.strategy.stop.StopStrategies;
import io.github.itning.retry.strategy.wait.WaitStrategies;
import lombok.extern.slf4j.Slf4j;

/**
 * 简单的Guava-Retry测试
 *
 * @author hubin
 * @date 2022年09月14日 10:39
 */
@Slf4j
public class GuavaRetryTest1 {
    @Test
    public void simpleTest1() {

        // RetryerBuilder 构建重试实例 retryer,可以设置重试源且可以支持多个重试源，可以配置重试次数或重试超时时间，以及可以配置等待时间间隔
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfExceptionOfType(RuntimeException.class) //设置异常重试源
                .retryIfResult(res -> res == false)  //设置根据结果重试
                .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS))   //设置等待间隔时间
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))  //设置最大重试次数
                .withRetryListener(new RetryListener() {   // 重试监听器
                                       @Override
                                       public <V> void onRetry(Attempt<V> attempt) {
                                           System.out.println();
                                           log.warn("第【{}】次调用失败", attempt.getAttemptNumber());
                                       }
                                   }
                )
                .build();

        try {
            retryer.call(() -> retryTask("abc"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 待重试的方法
     *
     * @param param
     * @return
     */
    public static boolean retryTask(String param) {
        log.info("收到请求参数:{}", param);

        int i = RandomUtils.nextInt(0, 4);
        log.info("随机生成的数:{}", i);
        if (i == 0) {
            log.warn("为0,抛出参数异常.");
            throw new IllegalArgumentException("参数异常");
        } else if (i == 1) {
            log.info("为1,返回true.");
            return true;
        } else if (i == 2) {
            log.info("为2,返回false.");
            return false;
        } else {
            //为其他
            log.warn("大于2,抛出自定义异常.");
            throw new RuntimeException("大于2,抛出远程访问异常");
        }
    }


}
