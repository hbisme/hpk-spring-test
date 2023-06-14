package priv.hb.sample.tool.vavr;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hubin
 * @date 2022年11月15日 09:13
 */
public class OptionTest {


    /**
     * 正常入参
     */
    @Test
    public void test1() {
        System.out.println(parseLocation("a,1,b c"));
        System.out.println(parseLocation2("a,1,b c"));
        System.out.println(parseLocation3("a,1,b c"));
    }


    /**
     * 当入参类型转换错误时
     */
    @Test
    public void test2() {
        System.out.println(parseLocation3("a,1a,b c"));
        System.out.println(parseLocation2("a,1a,b c"));
        System.out.println(parseLocation("a,1a,b c"));
    }


    /**
     * 当参数长度不够时
     */
    @Test
    public void test3() {
        System.out.println(parseLocation3("a,1,b"));
        System.out.println(parseLocation2("a,1,b"));
        System.out.println(parseLocation("a,1,b"));

    }


    /**
     * 举例的方法,有问题的方法,很多地方会有空指针的风险.
     * <p>
     * 输入: "a,1,b c"  => Location(a, 1, b)
     *
     * @param input
     * @return
     */
    public static Location parseLocation(String input) {
        String[] parts = input.split(",");
        List<String> partsList = List.ofAll(Arrays.stream(parts));

        String secondStr = partsList.get(2);
        String[] parts2 = secondStr.split(" ");
        return new Location(partsList.get(0), Integer.valueOf(partsList.get(1)), parts2[1]);
    }


    /**
     * 使用Option来判断参数的方法
     *
     * @param input
     * @return
     */
    public static Option<Location> parseLocation2(String input) {
        String[] parts = input.split(",");
        List<String> partsList = List.ofAll(Arrays.stream(parts));

        Option<String> value0 = getValue(partsList, 0);
        Option<String> value1 = getValue(partsList, 1);
        Option<String> value2 = getValue(partsList, 2);
        Option<Integer> integers = getIntegerValue(value1);

        Option<String> strings = value2.toTry().map(x -> x.split(" ")).map(x -> x[1]).toOption();


        if (value0.isDefined() && integers.isDefined() && strings.isDefined()) {
            return Option.of(new Location(value0.get(), integers.get(), strings.get()));
        }

        return Option.none();
    }


    public static Option<Integer> getIntegerValue(Option<String> input) {
        Try<String> strings = input.toTry();
        return strings.map(x -> Integer.valueOf(x)).toOption();
    }

    public static Option<String> getValue(List<String> list, Integer index) {
        if (list.size() > index) {
            return Option.of(list.get(index));
        } else {
            return Option.none();
        }
    }

    /**
     * 常规在原方法上加判断后的方法
     *
     * @param input
     * @return
     */
    public static Location parseLocation3(String input) {
        String[] parts = input.split(",");
        List<String> partsList = List.ofAll(Arrays.stream(parts));
        if (partsList.size() != 3) {
            return null;
        }

        String secondStr = partsList.get(2);
        String[] parts2 = secondStr.split(" ");
        if (parts2.length < 2) {
            return null;
        }

        String value2 = partsList.get(1);
        Integer integer;
        try {
            integer = Integer.valueOf(value2);
        } catch (NumberFormatException e) {
            System.out.println(value2 + "转换成数值非法");
            return null;
        }

        return new Location(partsList.get(0), integer, parts2[1]);
    }


}


@Data
@AllArgsConstructor
class Location {
    private String s1;
    private Integer i2;
    private String s3;
}



