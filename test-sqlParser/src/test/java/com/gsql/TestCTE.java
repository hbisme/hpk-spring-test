package com.gsql;

import org.junit.jupiter.api.Test;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 测试with语句
 * @author hubin
 * @date 2022年09月23日 09:23
 */
public class TestCTE {

    @Test
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext =
                "with tmp1 as ( select key from srcTable where key = '5')\n" +
                        "select *\n" +
                        "from tmp1;\n";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("tmp1"));
        assertTrue(select.getCteList().size() == 1);
        TCTE cte = select.getCteList().getCTE(0);
        assertTrue(cte.getTableName().toString().equalsIgnoreCase("tmp1"));
        TSelectSqlStatement s1 = cte.getSubquery();
        assertTrue(s1.tables.getTable(0).toString().equalsIgnoreCase("srcTable"));
    }



}
