package priv.hb.sample.sql.gsql;

import org.junit.jupiter.api.Test;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author hubin
 * @date 2022年09月22日 17:41
 */
public class Test1 {

    /**
     * 检查语法是否正确
     */
    @Test
    public void testCheckSyntax() {
        String sql = "create table emp1(id1 int,name1 varchar2(200),\n"
                + " money1 double(10,2),\n"
                + " PRIMARY KEY ( id1 ));";
        TGSqlParser mysqlSqlParser = new TGSqlParser(EDbVendor.dbvmysql);
        mysqlSqlParser.sqltext = sql;
        int rs = mysqlSqlParser.checkSyntax();
        if (rs == 0) {
            System.out.println("语法正确！");
        } else {
            System.out.println("语法错误：" + mysqlSqlParser.getErrormessage());
        }
    }


    @Test
    public void testFormatSQL() {
        String sql = "insert into emp(empno,empnm,deptnm,sal) select empno, empnm, dptnm, sal from emp where empno=:empno;\n" +
                "\n" +
                "select empno, empnm from (select empno, empnm from emp)";

        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmysql);

        sqlParser.sqltext = sql;

        sqlParser.parse();

        GFmtOpt option = GFmtOptFactory.newInstance();
        String formatSQL = FormatterFactory.pp(sqlParser, option);

        System.out.println("格式化后的SQL：\n" + formatSQL);
    }

    @Test
    public void test2() {
        // @formatter:off
        String sql =
                 " -- 多条SQL语句; \n"
               + "alter table tab1 add (author_last_published2 date); \n"//添加1个字段
               + " alter table tab2 add (author_last_published3 date, age number(10,2)); -- 添加两个字段\n"//添加多个字段
               + "drop table tab3; -- 删除表; \n";    //删表;
        // @formatter:on

        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlParser.sqltext = sql;
        sqlParser.parse();

        TStatementList statementList = sqlParser.sqlstatements;

        System.out.println("共有条" + statementList.size() + "sql语句!");

        for (TCustomSqlStatement statement : statementList) {
            System.out.println(statement);
        }
    }


    @Test
    public void test3() {
        String sql1 = "CREATE TABLE tmp.jinpushi_066\n" +
                "AS\n" +
                "SELECT \n" +
                "T1.a1, " +
                "T1.a2, " +
                "T1.a3, " +
                "T1.a4 " +
                ",T2.b1" +
                ",T2.b2 \n" +
                ",T3.b3\n" +
                ",T2.b4\n" +
                ",T1.a5\n" +
                "FROM dwd.jinpushi_01 T1\n" +
                "LEFT JOIN dim.jinpushi_02 T2\n" +
                "ON T1.id = T2.id\n" +
                "LEFT JOIN ods.jinpushi_03 T3\n" +
                "ON T1.code = T3.value\n" +
                "AND T3.tyoe = 12306\n" +
                ";\n" +

                "INSERT OVERWRITE TABLE dwb.jinpushi_05 \n" +
                "SELECT * FROM tmp.jinpushi_066\n" +
                ";";

        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvhive);
        sqlParser.sqltext = sql1;

        sqlParser.parse();

        TStatementList sqlstatements = sqlParser.sqlstatements;

        TCustomSqlStatement tCustomSqlStatement = sqlstatements.get(0);

        TSelectSqlStatement tCustomSqlStatement1 = ((TCreateTableSqlStatement) tCustomSqlStatement).getSubQuery();


        System.out.println();
    }


    @Test
    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "INSERT OVERWRITE LOCAL DIRECTORY '/tmp/ttt' SELECT * from (select * from a) c";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement) sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select = insertSqlStatement.getSubQuery();
        TTable table = select.tables.getTable(0);
        System.out.println(table.getFullName());
    }

    /**
     * 测试修改SQL有问题,没成功
     */
    @Test
    public void testModifySql() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "select id,name from t1 join t2 on t1.c1 = t2.c2";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
        TExpression tExpression = new TExpression();
        tExpression.setString("name2");
        select.getResultColumnList().getResultColumn(1).setExpr(tExpression);
        System.out.println(select.toString());



    }


}
