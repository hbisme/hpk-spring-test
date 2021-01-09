package other;

import java.util.Date;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

public class TestTimeParser {
    public static void main(String[] args) {
        String dateStr = "2017-03-01";
        Date date = DateUtil.parse(dateStr, "yyyy-MM-dd");
        System.out.println(date);

        String input = "22/09/2020:12:19:51 +0800";
        Long current = 1600748393000L;
        // Long res = t.eval(input, "dd/MMM/yyyy:hh:mm:ss Z");
        DateTime d1 = DateUtil.parse(input, "dd/MM/yyyy:HH:mm:ss Z");
        System.out.println(d1);

    }
}
