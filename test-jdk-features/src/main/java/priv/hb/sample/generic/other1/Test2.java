package priv.hb.sample.generic.other1;

import java.util.ArrayList;

/**
 * 泛型传递测试,方法参数clazz来确定方法返回类型
 */
public class Test2 {

    /**
     * 通过传入的clazz参数来确定泛型T,虽然后面clazz没有做其他用途
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> genList(Class<T> clazz) {
        ArrayList<T> list = new ArrayList<>();
        return list;
    }


    public static void main(String[] args) {
        Class<Integer> integerClass = Integer.class;

        ArrayList<Integer> integers = genList(integerClass);
        integers.add(1);
        System.out.println(integers);

        Class<String> classa = String.class;
        ArrayList<String> strings = genList(classa);
        strings.add("a");
        System.out.println(strings);

    }
}
