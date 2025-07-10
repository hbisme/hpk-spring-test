package priv.hb.sample.tool.vavr;

import java.io.IOException;

import org.junit.Test;

import io.vavr.CheckedFunction1;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;



/**
 * @author hubin
 * @date 2024年09月08日 01:42
 */
@Slf4j
public class CheckedTest {


    /**
     * 如果你确定你的映射函数不会抛出受检异常，使用 map() 是个好选择。
     * 如果你的映射函数可能抛出受检异常，或者你想更明确地表达"这个操作可能失败"的语义，使用 mapTry() 更合适。
     */
    @Test
    public void testMapTry() {
        Try<String> tryValue = Try.of(() -> "5");

        // 使用 map()
        Try<Integer> mapResult = tryValue.map(s -> {
            // 这里我们不能抛出受检异常，否则会编译错误
            // 如果这里抛出运行时异常，它会被捕获并转换为 Failure
            // throw new IOException("Simulated IO exception");
            return Integer.parseInt(s);
        });

        // 使用 mapTry()
        Try<Integer> mapTryResult = tryValue.mapTry(s -> {

            // 这里我们可以使用sleep,不用try-catch,因为mapTry会捕获异常并转换为 Failure
            Thread.sleep(10);

            // 这里我们可以抛出受检异常，它会被捕获并转换为 Failure
            if (s.equals("5")) {
                throw new IOException("Simulated IO exception");
            }
            return Integer.parseInt(s);
        });

        log.info("map result: " + mapResult);
        log.error("mapTry result: " + mapTryResult);
    }


    /**
     * Function1：用于不抛出受检异常的函数式接口。适用于处理简单的、不可能失败的转换。
     * CheckedFunction1：用于可能抛出受检异常的函数式接口。适用于处理可能失败的操作，如 IO 操作、网络请求等。
     */
    @Test
    public void checkedFunction1Test() {
        // 普通的Function1是不能抛出异常的
        CheckedFunction1<Integer, Integer> parseAndSquare = x -> {
            if (x == null) {
                throw new IllegalArgumentException("Input cannot be null");
            }
            return x * x;
        };


        Try<Integer> result = Try.of(() -> parseAndSquare.apply(5));
        System.out.println(result); // Output: Success(25)

        Try<Integer> failedResult = Try.of(() -> parseAndSquare.apply(null));
        System.out.println(failedResult); // Output: Failure(java.lang.IllegalArgumentException: Input cannot be null)
    }
}
