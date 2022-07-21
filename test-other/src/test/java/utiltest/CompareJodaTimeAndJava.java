package utiltest;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 比较JodaTime 和 java8的time
 * 结论: 两者差不多,用java8的LocalDateTime就够了
 *
 */
public class CompareJodaTimeAndJava {

    /**
     * 时间字符串加秒后的时间字符串
     */
    public static String timeStringPlusSecond(String time, Integer seconds) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dt = LocalDateTime.parse(time, pattern);
        LocalDateTime localDateTime = dt.plusSeconds(seconds);
        return localDateTime.format(pattern);
    }


    public static String timeStringPlusSecondJoda(String time, Integer seconds) {
        org.joda.time.format.DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime dateTime = DateTime.parse(time, dateTimeFormatter);
        DateTime dateTime1 = dateTime.plusSeconds(seconds);
        return dateTime1.toString(dateTimeFormatter);
    }

    @Test
    public void test1() {
        String time = "2022-07-21 10:00:00";
        System.out.println(timeStringPlusSecond(time, 61));
        System.out.println(timeStringPlusSecondJoda(time, 61));
    }


    /**
     * 是否给定的时间字符串比现在时间要晚
     */
    public static boolean ifTimeStringAfterNow(String time) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dt = LocalDateTime.parse(time, pattern);
        LocalDateTime now = LocalDateTime.now();
        return dt.isAfter(now);
    }


    public static boolean ifTimeStringAfterNowJoda(String time) {
        org.joda.time.format.DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime dateTime = DateTime.parse(time, dateTimeFormatter);
        DateTime now = DateTime.now();
        return dateTime.isAfter(now);
    }

    @Test
    public void test2() {
        String time = "2022-08-21 10:00:00";
        System.out.println(ifTimeStringAfterNow(time));
        System.out.println(ifTimeStringAfterNowJoda(time));
    }

    public static boolean ifTime1AfterTime2(String time1, String time2) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse1 = LocalDateTime.parse(time1, pattern);
        LocalDateTime parse2 = LocalDateTime.parse(time2, pattern);
        return parse1.isAfter(parse2) ? true : false;
    }

    public static boolean ifTime1AfterTime2Joda(String time1, String time2) {
        org.joda.time.format.DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime dateTime1 = DateTime.parse(time1, dateTimeFormatter);
        DateTime dateTime2 = DateTime.parse(time2, dateTimeFormatter);
        return dateTime1.isAfter(dateTime2) ? true : false;
    }

    @Test
    public void test3() {
        String time1 = "2022-08-21 10:00:00";
        String time2 = "2022-08-21 11:00:00";

        System.out.println(ifTime1AfterTime2(time1, time2));
        System.out.println(ifTime1AfterTime2Joda(time1, time2));
    }


}
