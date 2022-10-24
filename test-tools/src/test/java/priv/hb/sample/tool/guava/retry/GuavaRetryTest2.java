package priv.hb.sample.tool.guava.retry;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.itning.retry.Retryer;
import io.github.itning.retry.RetryerBuilder;
import io.github.itning.retry.strategy.stop.StopStrategies;
import io.github.itning.retry.strategy.wait.WaitStrategies;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * 比较有用的guava重试测试,
 * 获取分页数据,获取每个分页数据都可能异常,需要重试.
 * 结合vavr的Try 和 Guava的retry 结合 将分页结果合并起来.
 *
 * @author hubin
 * @date 2022年09月14日 10:39
 */
@Slf4j
public class GuavaRetryTest2 {

    @Test
    public void RetryWithTryTest() {
        io.vavr.collection.List<Integer> range = io.vavr.collection.List.range(1, 10);

        List<List<Integer>> list = range.map(x -> getPageResult(x)).toJavaList();

        System.out.println(list);

    }


    public List<Integer> getPageResult(Integer pageId) {

        Retryer<List<Integer>> retryer = RetryerBuilder.<List<Integer>>newBuilder()
                .retryIfException()
                .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS))   //设置等待间隔时间
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))  //设置最大重试次数
                .build();


        Try<List<Integer>> of = Try.of(() -> {
            List<Integer> call = retryer.call(() -> getPageInfo(pageId));
            return call;
        });

        List<Integer> recoverValue = io.vavr.collection.List.of(-1).toJavaList();
        Try<List<Integer>> recover = of.recover((Throwable t) -> {
            log.error("读取分页重试后也异常,返回默认值");
            return recoverValue;
        });

        List<Integer> result = recover.get();
        return result;

    }


    public static List<Integer> getPageInfo(Integer pageId) {
        System.out.println();
        log.info("收到请求参数pagId: {}", pageId);

        int i = RandomUtils.nextInt(0, 3);
        log.info("随机生成的数:{}", i);

        if (i == 0) {
            log.warn("为0,抛出参数异常.");
            throw new IllegalArgumentException("参数异常");
        } else if (i == 1) {
            log.info("为1,返回正常值.");
            return io.vavr.collection.List.of(pageId, i).toJavaList();
        } else if (i == 2) {
            log.warn("为2,抛出运行异常.");
            throw new RuntimeException("运行异常");
        } else {
            //为其他
            log.warn("大于2,抛出自定义异常.");
            throw new RuntimeException("大于2,抛出远程访问异常");
        }

    }

}
