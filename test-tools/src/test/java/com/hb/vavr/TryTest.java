package com.hb.vavr;


import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;

import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.Try;
import static io.vavr.Predicates.instanceOf;


/**
 * @author hubin
 * @date 2022年10月13日 10:33
 */
@Log
public class TryTest {

    @Test
    public void testTry() {
        Try<Integer> computation = Try.of(() -> 1 / 0);
        int errorSentinel = computation.getOrElse(-1);
        Assert.assertEquals(-1, errorSentinel);
    }


    /**
     * 测试try的异常恢复(比较有用)
     */
    @Test
    public void testTryRecover() {
        Try<Integer> aTry = Try(() ->
        {
            return echo(-1);
        });

        Integer defaultValue = 0;
        Function<Throwable, Integer> recoverFunction = errorRecoverFunction(defaultValue);
        Try<Integer> result = aTry.recover(recoverFunction);
        System.out.println(result.getOrElse(defaultValue));

    }


    public static Function errorRecoverFunction(Object defaultValue) {
        Function func = x -> Match(x).of(
                Case($(instanceOf(NullPointerException.class)), error -> {
                    log.warning("失败1,错误信息: " + error.getMessage() + ",故使用默认值: " + defaultValue);
                    return defaultValue;
                }),
                Case($(instanceOf(IllegalArgumentException.class)), error -> {
                    log.warning("失败2,错误信息: " + error.getMessage() + ",故使用默认值: " + defaultValue);
                    return defaultValue;
                })
        );

        return func;
    }


    /**
     * 测试过滤掉Failed的结果
     */
    @Test
    public void test2() {
        List<Integer> list = List.range(0, 100).map(x -> ((int) (Math.random() * 100 - 50)));
        System.out.println(list);

        List<Try<Integer>> result = list.map(x -> {
            Try<Integer> aTry = Try(() ->
            {
                return echo(x);
            });
            return aTry;
        });

        result.filter(x -> x.isSuccess()).forEach(x -> System.out.println(x));

    }




    /**
     * 一个会有抛异常情况的函数
     *
     * @param input
     * @return
     */
    public Integer echo(Integer input) {
        if (input > -50 && input < 0) {
            throw new NullPointerException();
        }
        if (input < -100) {
            throw new IllegalArgumentException();
        }
        return input * 2;
    }


}
