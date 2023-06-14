package priv.hb.sample.tool.vavr.monad;

import java.util.function.Function;

/**
 * 描述的是依赖共享环境的计算
 *
 * @author hubin
 * @date 2022年10月28日 15:45
 */
public class ReaderMonad {

    public static <E, T> Function<E, T> unit(T value) {
        return e -> value;
    }

    public static <T1, T2, E> Function<E, T2> bind(Function<E, T1> input, Function<T1, Function<E, T2>> transform) {
        return e -> transform.apply(input.apply(e)).apply(e);
    }

    public static void main(String[] args) {
        Function<Environment, String> input = unit("Hello");
        Function<Environment, String> bind1 = bind(input, value -> environment -> environment.getPrefix() + value);
        Function<Environment, Integer> bind2 = bind(bind1, value -> environment -> environment.getBase() + value.length());

        Integer apply = bind2.apply(new Environment());
        System.out.println(apply);

    }
}
