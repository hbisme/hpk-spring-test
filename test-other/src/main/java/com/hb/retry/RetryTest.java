package com.hb.retry;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 测试使用guava来 重试.
 */
public class RetryTest {
    private static int count = 0;

    // 定义实现Callable接口的方法，以便Guava retryer能够调用
    private static Callable<Boolean> updateReimAgentsCall = new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            count++;
            System.out.println("call times:" + count);
            if (count > 10) {
                return true;
            }

            return false;
        }
    };

    public static void main(String[] args) {
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfException()
                //返回false也需要重试
                .retryIfResult(Predicates.equalTo(false))
                //重调策略
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                //尝试次数
                .withStopStrategy(StopStrategies.stopAfterAttempt(30))
                .build();

        try {
            retryer.call(updateReimAgentsCall);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (RetryException e) {
            e.printStackTrace();
        }


    }

}
