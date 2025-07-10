package priv.hb.sample;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

/**
 * @author hubin
 * @date 2024年03月14日 14:16
 */
public class TestBigDecimal {

    /*
         浮点数 float 或 double 运算的时候会有精度丢失的风险
     */
    @Test
    public void test1() {
        float a = 2.0f - 1.9f;
        float b = 1.8f - 1.7f;
        System.out.println(a); // 0.100000024
        System.out.println(b); // 0.099999905
        System.out.println(a == b); // false    }
    }


    /**
     * BigDecimal对象构造测试
     * 必须使用它的BigDecimal(String val)构造方法或者 BigDecimal.valueOf(double val) 静态方法来创建对象。
     */
    @Test
    public void test2() {
        BigDecimal a = new BigDecimal("1.0");
        BigDecimal b = new BigDecimal("0.9");
        BigDecimal c = new BigDecimal("0.8");

        BigDecimal x = a.subtract(b);
        BigDecimal y = b.subtract(c);

        System.out.println(x.compareTo(y));// 0
    }


    /**
     * 加减乘除方法测试
     * divide 方法用于将两个 BigDecimal 对象相除
     * 使用 divide 方法的时候尽量使用 3 个参数版本，并且RoundingMode 不要选择 UNNECESSARY，否则很可能会遇到 ArithmeticException（无法除尽出现无限循环小数的时候），其中 scale 表示要保留几位小数，roundingMode 代表保留规则。
     */
    @Test
    public void test3() {
        BigDecimal a = new BigDecimal("1.0");
        BigDecimal b = new BigDecimal("0.9");
        System.out.println(a.add(b));// 1.9
        System.out.println(a.subtract(b));// 0.1
        System.out.println(a.multiply(b));// 0.90
        // System.out.println(a.divide(b));// 无法除尽，抛出 ArithmeticException 异常
        System.out.println(a.divide(b, 2, RoundingMode.HALF_UP));// 1.11
    }


    /**
     * BigDecimal之间的判等和比较要使用compareTo不能使用equals
     * a.compareTo(b) : 返回 -1 表示 a 小于 b，0 表示 a 等于 b ， 1 表示 a 大于 b。
     */
    @Test
    public void test4() {
        BigDecimal a = new BigDecimal("1.0");
        BigDecimal b = new BigDecimal("0.9");
        System.out.println(a.compareTo(b));// 1
    }

    /**
     * 保留几位小数
     */
    @Test
    public void test5() {
        BigDecimal m = new BigDecimal("1.255433");
        BigDecimal n = m.setScale(3, RoundingMode.HALF_DOWN);
        System.out.println(n);// 1.255
    }



}
