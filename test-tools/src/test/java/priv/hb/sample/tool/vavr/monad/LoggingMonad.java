package priv.hb.sample.tool.vavr.monad;


import java.util.function.Function;

import io.vavr.collection.List;

/**
 * 一个 Monad 的定义中包含了 3 个要素。在定义 Monad 时需要提供一个类型构造器 M 和两个操作 unit 和 bind：
 *
 *
 * 类型构造器的作用是从底层的类型中创建出一元类型（monadic type）。如果 M 是 Monad 的名称，而 t 是数据类型，则 M t 是对应的一元类型。
 * unit 操作把一个普通值 t 通过类型构造器封装在一个容器中，所产生的值的类型是 M t。unit 操作也称为 return 操作。return 操作的名称来源于 Haskell。不过由于 return 在很多编程语言中是保留关键词，用 unit 做名称更为合适。
 * bind 操作的类型声明是 (M t)→(t→M u)→(M u)。该操作接受类型为 M t 的值和类型为 t → M u 的函数来对值进行转换。
 * 在进行转换时，bind 操作把原始值从容器中抽取出来，再应用给定的函数进行转换。函数的返回值是一个新的容器值 M u。M u 可以作为下一次转换的起点。多个 bind 操作可以级联起来，形成处理流水线。
 *
 *
 * @author hubin
 * @date 2022年10月28日 15:08
 */
public class LoggingMonad<T> {
    private final T value;

    private final List<String> logs;

    public LoggingMonad(T value, List<String> logs) {
        this.value = value;
        this.logs = logs;
    }

    @Override
    public String toString() {
        return "LoggingMonad{" +
                "value=" + value +
                ", logs=" + logs +
                '}';
    }

    public static <T> LoggingMonad<T> unit(T value) {
        return new LoggingMonad(value, List.empty());
    }


    public static <T1, T2> LoggingMonad<T2> bind(LoggingMonad<T1> input, Function<T1, LoggingMonad<T2>> transform) {
        LoggingMonad<T2> result = transform.apply(input.value);
        List<String> rLog = result.logs;
        List<String> logs = input.logs.appendAll(rLog);

        return new LoggingMonad(result.value, logs);
    }


    public static <T> LoggingMonad<T> pipeline(LoggingMonad<T> monad, List<Function<T, LoggingMonad<T>>> transforms) {
        LoggingMonad<T> result = monad;
        List<String> logs = monad.logs;

        for (Function<T, LoggingMonad<T>> transform : transforms) {
            result = bind(result, transform);
        }

        return result;
    }


    public static void main(String[] args) {
        Function<Integer, LoggingMonad<Integer>> transform1 = v -> new LoggingMonad<>(v * 4, List.of(v + " * 4"));
        Function<Integer, LoggingMonad<Integer>> transform2 = v -> new LoggingMonad<>(v / 2, List.of(v + " / 2"));


        System.out.println("union: " + LoggingMonad.unit("a"));
        LoggingMonad<Integer> bind = LoggingMonad.bind(LoggingMonad.unit(2), transform1);
        System.out.println("bind: " + bind);

        LoggingMonad<Integer> pipeline = pipeline(LoggingMonad.unit(1), List.of(transform1, transform2));
        System.out.println(pipeline);

    }


}
