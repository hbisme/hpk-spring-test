package priv.hb.sample.util;

import org.apache.calcite.sql.SqlNode;
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment;
import org.apache.flink.table.api.bridge.scala.internal.StreamTableEnvironmentImpl;
import org.apache.flink.table.delegation.Parser;

import org.apache.flink.table.planner.calcite.CalciteParser;
import org.apache.flink.table.planner.delegation.ParserImpl;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import io.vavr.Tuple2;

import static io.vavr.API.Tuple;

/**
 * flink-Sql 验证工具类, 需要传入tEnv里的parser.
 *
 * @author hubin
 * @date 2022年11月01日 14:35
 */
public class ParserCommon {
    public static CalciteParser get(Parser parser) throws NoSuchFieldException, IllegalAccessException {
        ParserImpl object = ((ParserImpl) parser);
        Class<? extends ParserImpl> aClass = object.getClass();
        Field field = aClass.getDeclaredField("calciteParserSupplier");
        field.setAccessible(true);
        Supplier<CalciteParser> calciteParser = (Supplier<CalciteParser>) field.get(object);
        return calciteParser.get();
    }

    public static Tuple2<Boolean, String> ifVaildSql(String sql, Parser object) throws NoSuchFieldException, IllegalAccessException {

        CalciteParser calciteParser = get(object);

        try {
            SqlNode parse = calciteParser.parse(sql);
            return Tuple(true, "");
        } catch (RuntimeException e) {
            return Tuple(false, e.getMessage());
        }
    }

    public static Tuple2<Boolean, String> ifVaildSql(String sql, StreamTableEnvironment tEnv) throws NoSuchFieldException, IllegalAccessException {
        StreamTableEnvironmentImpl impl = (StreamTableEnvironmentImpl) tEnv;
        Parser parser = impl.getParser();

        CalciteParser calciteParser = get(parser);

        try {
            SqlNode parse = calciteParser.parse(sql);
            return Tuple(true, "");
        } catch (RuntimeException e) {
            return Tuple(false, e.getMessage());
        }


    }

}
