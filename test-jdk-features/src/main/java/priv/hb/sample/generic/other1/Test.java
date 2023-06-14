package priv.hb.sample.generic.other1;

import java.util.Date;

/**
 * @author hubin
 * @date 2022年11月23日 10:56
 */
public class Test {
    public static void main(String[] args) throws Exception {

        Class<Date> dateClass = Date.class;
        Date instance1 = getInstance1(dateClass);
        System.out.println(instance1);

    }


    public static <T> T getInstance1(Class<T> clz) throws Exception{
        return getInstance2(clz);
    }


    public static <T> T getInstance2(Class<T> clz) throws Exception {
        return clz.newInstance();
    }



}
