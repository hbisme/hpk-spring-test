package priv.hb.sample.tool.vavr;

import java.util.Properties;

import org.junit.Test;

import io.vavr.control.Either;

/**
 * Try和Either在某些情况下可以互换使用，但它们的设计初衷和使用场景是不同的。Try更适合处理可能会抛出异常的代码，而Either更适合表示可能返回两种不同类型结果的函数。根据具体的需求选择合适的工具，才能更好地编写出清晰和健壮的代码。
 * 总结: 当你的代码需要处理多种可能性结果，并且希望根据不同的结果执行不同的逻辑时，使用 Either 比 Try 更合适。它更强调结果的选择性，而不是仅仅关注异常处理。
 *
 * @author hubin
 * @date 2024年06月03日 20:40
 */
public class EitherDifferentTry {
    public static Either<String, Integer> validateAge(String input) {
        try {
            int age = Integer.parseInt(input);
            if (age < 0) {
                return Either.left("年龄不能为负数");
            } else {
                return Either.right(age);
            }
        } catch (NumberFormatException e) {
            return Either.left("输入不是有效的整数");
        }
    }


    /**
     * 数据校验和转换
     */
    @Test
    public void test1() {
        Either<String, Integer> result = validateAge("-25");

        if (result.isRight()) {
            System.out.println("有效年龄: " + result.get());
        } else {
            System.out.println("错误信息: " + result.getLeft());
        }
    }


    // 假设你需要将一个字符串解析为整数，并检查它是否在有效范围内

    public Either<String, Integer> parseAndValidate(String input) {
        try {
            int value = Integer.parseInt(input);
            if (value >= 1 && value <= 100) {
                return Either.right(value); // 返回有效值
            } else {
                return Either.left("Value must be between 1 and 100"); // 返回业务错误信息
            }
        } catch (NumberFormatException e) {
            return Either.left("Invalid integer format"); // 返回系统错误信息
        }
    }

    @Test
    public void test2() {
        Either<String, Integer> result = parseAndValidate("123");
        if (result.isRight()) {
            System.out.println("Valid value: " + result.get());
        } else {
            System.out.println("Error: " + result.getLeft());
        }

    }


}




