package priv.hb.sample.tool.vavr;

import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;

import io.vavr.API;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.extern.java.Log;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.Tuple;
import static io.vavr.API.run;
import static io.vavr.Predicates.allOf;
import static io.vavr.Predicates.anyOf;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.Predicates.is;
import static io.vavr.Predicates.isIn;
import static io.vavr.Predicates.isNotNull;
import static io.vavr.Predicates.isNull;
import static io.vavr.Predicates.noneOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
// import static javaslang.Patterns.*;


/**
 * $() - 全匹配
 * $(value) - 匹配特定值
 * $(predicate) - conditional pattern
 *
 * @author hubin
 * @date 2022年08月29日 16:00
 */
@Log
public class MatchTest {

    /**
     * 特定值匹配模式
     */
    @Test
    public void test1() {
        Integer i = 2;

        String output = Match(i).of(
                Case($(1), "one"),
                Case($(2), "two"),
                Case($(), "?")
        );
        assertEquals("two", output);
    }


    @Test
    public void whenMatchWorksWithOption() {
        int i = 10;
        Option<String> s = Match(i).option(Case($(0), "zero"));
        assertTrue(s.isEmpty());
        assertEquals("None", s.toString());
    }


    /**
     * vavr 内置谓语"instanceOf"的测试
     */
    @Test
    public void givenInput_whenMatchesClass_thenCorrect() {
        Object obj = "abc";
        String s = Match(obj).of(
                Case($(instanceOf(String.class)), "string matched"),
                Case($(), "not string"));

        assertEquals("string matched", s);
    }

    /**
     * vavr 内置谓语"isNull", "isNotNull" 的测试
     */
    @Test
    public void givenInput_whenMatchesNull_thenCorrect() {
        Object obj = 5;
        String s = Match(obj).of(
                Case($(isNull()), "no value"),
                Case($(isNotNull()), "value found"));

        assertEquals("value found", s);
    }


    /**
     * vavr 内置谓语"isIn" 的测试
     */
    @Test
    public void givenInput_whenContainsWorks_thenCorrect() {
        int i = 5;
        String s = Match(i).of(
                Case($(isIn(2, 4, 6, 8)), "Even Single Digit"),
                Case($(isIn(1, 3, 5, 7, 9)), "Odd Single Digit"),
                Case($(), "Out of range"));

        assertEquals("Odd Single Digit", s);
    }


    /**
     * vavr 内置谓语"anyOf" 的测试
     */
    @Test
    public void givenInput_whenMatchesAnyOfWorks_thenCorrect() {
        Integer year = 1990;
        String s = Match(year).of(
                Case($(anyOf(isIn(1990, 1991, 1992), is(1986))), "Age match"),
                Case($(), "No age match"));
        assertEquals("Age match", s);
    }


    /**
     * vavr 内置谓语"noneOf"(单个case中的都不匹配) 的测试
     */
    @Test
    public void givenInput_whenMatchesNoneOfWorks_thenCorrect() {
        Integer year = 1987;
        String s = Match(year).of(
                Case($(noneOf(isIn(1990, 1991, 1992), is(1986))), "Age match"),
                Case($(), "No age match"));

        assertEquals("Age match", s);
    }

    /**
     * vavr 内置谓语"allOf"(单个case中的条件都匹配) 的测试
     */
    @Test
    public void givenInput_whenMatchAllWorks_thenCorrect() {
        Integer i = 2;
        String s = Match(i).of(
                Case($(allOf(isNotNull(), isIn(1, 2, 3))), "Number found"),
                Case($(), "Not found"));

        assertEquals("Number found", s);
    }

    /**
     * 自定义条件的测试
     */
    @Test
    public void whenMatchWorksWithCustomPredicate_thenCorrect() {
        int i = 3;
        String s = Match(i).of(
                Case($(n -> n == 1), "one"),
                Case($(n -> n == 2), "two"),
                Case($(n -> n == 3), "three"),
                Case($(), "?"));
        assertEquals("three", s);
    }

    /**
     * 自定义条件的测试2
     * 比较有用
     */
    @Test
    public void whenMatchWorksWithCustomPredicate_thenCorrect_2() {
        Tuple2<Integer, String> tuple = Tuple(1, "a");
        Tuple2<Integer, String> res = Match(tuple).of(
                Case($(n -> n._1 == 1 && n._2.equals("a")), x -> x),
                Case($(n -> n._1 == 2), x -> x),
                Case($(n -> n._1 == 3), x -> x),
                Case($(), Tuple(-1, "")));
        System.out.println(res);
    }


    @Test
    public void givenInput_whenContainsWorks_thenCorrect2() {
        int i = 5;
        BiFunction<Integer, List<Integer>, Boolean> contains
                = (t, u) -> u.contains(t);

        String s = Match(i).of(
                Case($(o -> contains.apply(i, API.List(2, 4, 6, 8))), "Even Single Digit"),
                Case($(o -> contains.apply(i, API.List(1, 3, 5, 7, 9))), "Odd Single Digit"),
                Case($(), "Out of range"));
        assertEquals("Odd Single Digit", s);
    }


    @Test
    public void givenInput_whenMatchAllWorks_thenCorrect2() {
        Integer i = 2;
        Integer of = Match(i).of(
                Case($(allOf(isNotNull(), isIn(1, 2, 3))), x -> x * 2),
                Case($(), -1));
    }


    /**
     * Case匹配后执行副作用
     */
    @Test
    public void whenMatchCreatesSideEffects_thenCorrect() {
        int i = 4;
        Match(i).of(
                Case($(isIn(2, 4, 6, 8)), o -> run(() -> System.out.println(o))),
                Case($(isIn(1, 3, 5, 7, 9)), o -> run(this::displayOdd)),
                Case($(), o -> run(() -> {
                    throw new IllegalArgumentException(String.valueOf(i));
                })));
    }

    public void displayOdd() {
        System.out.println("Input is odd");
    }

    @Test
    public void testInstanceOf() {
        Object obj = 3;
        Number f = Match(obj).of(
                Case($(instanceOf(Integer.class)), x -> x + 1),
                Case($(instanceOf(Double.class)), x -> x + 3),
                Case($(), o -> {
                    throw new NumberFormatException();
                })
        );
        System.out.println(f);
    }




    // 类析构有问题
    // static Tuple2<String, String> Employee(Employee Employee) {
    //     return Tuple.of(Employee.getName(), Employee.getId());
    // }
    //
    //
    // @Test
    // public void givenObject_whenDecomposesVavrWay_thenCorrect() {
    //     Employee person = new Employee("Carl", "EMP01");
    //
    //     String result = Match(person).of(
    //             Case(Employee($("Carl"), $()),
    //                     (name, id) -> "Carl has employee id "+id),
    //             Case($(),
    //                     () -> "not found"));
    //
    //     assertEquals("Carl has employee id EMP01", result);
    // }
    //
    //
    // static Tuple3<Integer, Integer, Integer> LocalDate(LocalDate date) {
    //     return Tuple.of(
    //             date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    // }
    //
    // @Test
    // public void givenObject_whenDecomposesVavrWay_thenCorrect2() {
    //     LocalDate date = LocalDate.of(2017, 2, 13);
    //
    //     String result = Match(date).of(
    //             Case(LocalDate($(2016), $(3), $(13)),
    //                     () -> "2016-02-13"),
    //             Case(LocalDate($(2016), $(), $()),
    //                     (y, m, d) -> "month " + m + " in 2016"),
    //             Case(LocalDate($(), $(), $()),
    //                     (y, m, d) -> "month " + m + " in " + y),
    //             Case($(),
    //                     () -> "(catch all)")
    //     );
    //
    //     assertEquals("month 2 in 2017",result);
    // }


    // @Test
    // public void testIn() {
    //     String s = "";
    //     Match(s).of(
    //             Case(isIn("-h", "--help"), o -> run(() -> System.out.println(o)))
    //     )
    //
    //
    //
    //     Match(s).of(
    //             Case(isIn("-h", "--help"), o -> run(this::displayHelp)),
    //             Case(isIn("-v", "--version"), o -> run(this::displayVersion)),
    //             Case($(), o -> { throw new IllegalArgumentException(s); })
    //     );
    //
    // }


}
