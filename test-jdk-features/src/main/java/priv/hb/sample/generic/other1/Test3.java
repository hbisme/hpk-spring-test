package priv.hb.sample.generic.other1;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试从构造方法传class对象给类泛型<T>, 结果不行.
 * 必须要声明T的具体类型.
 *
 */
public class Test3<T> {


    private Class<T> clazz;

    public Test3(Class<T> clazz) {
        this.clazz = clazz;
    }


    public List<T> genList() {
        ArrayList<T> list = new ArrayList<>();
        return list;

    }


    public static void main(String[] args) {
        Test3<Integer> test3 = new Test3(Integer.class);
        List<Integer> list = test3.genList();
        list.add(1);
        System.out.println(list);
    }


}
