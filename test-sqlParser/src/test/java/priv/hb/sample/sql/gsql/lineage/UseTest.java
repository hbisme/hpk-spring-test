package priv.hb.sample.sql.gsql.lineage;

import org.junit.jupiter.api.Test;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * USE语句的类型是 gudusoft.gsqlparser.stmt.hive.THiveSwitchDatabase
 * @author hubin
 * @date 2022年10月09日 18:06
 */
public class UseTest {
    @Test
    public void test1() {
        String sql = "USE testdb;";

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sql;
        assertTrue(sqlparser.parse() == 0);

        TStatementList sqlstatements = sqlparser.getSqlstatements();
        TCustomSqlStatement tCustomSqlStatement = sqlstatements.get(0);
        System.out.println(tCustomSqlStatement);

    }
}
