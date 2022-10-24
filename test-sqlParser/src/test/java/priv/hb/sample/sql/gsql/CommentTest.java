package priv.hb.sample.sql.gsql;

import org.junit.jupiter.api.Test;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import priv.hb.sample.sql.gsql.utils.Common;

/**
 * @author hubin
 * @date 2022年10月09日 20:28
 */
public class CommentTest {
    /**
     * 测试去除掉注释(单行注释和多行注释)
     */
    @Test
    public void testRemoveComment() {
        String sql1 =
                "select t1, '--t2,'--this is a comment1   \n" +
                        ", 't3' from table1 --this is a comment2 \n" +
                        "where id >1 /* this is a comment3  */ \n" +
                        "and name = hb";

        String sql2 = "SELECT * FROM Students;\n" +
                "SELECT * FROM /* STUDENT_DETAILS;\n" +
                "SELECT * FROM Orders;\n" +
                "SELECT * FROM */ Articles; ";

        System.out.println(sql1);
        System.out.println("-----");
        String presult1 = Common.removeComments(sql1);
        System.out.println(presult1);

        System.out.println("=======\n");

        System.out.println(sql2);
        System.out.println("-----");
        String presult2 = Common.removeComments(sql1);
        System.out.println(presult2);

    }

    /**
     * 格式化SQL
     */
    @Test
    public void testFormat() {
        String sql = "insert into emp(empno,empnm,deptnm,sal) select empno, empnm, dptnm, sal from emp where empno=:empno;\n" +
                "\n" +
                "select empno, empnm from (select empno, empnm from emp)";


        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlParser.sqltext = sql;
        sqlParser.parse();

        GFmtOpt option = GFmtOptFactory.newInstance();
        String formatSQL = FormatterFactory.pp(sqlParser, option);
        System.out.println("格式化后的SQL：\n"+formatSQL);
    }

    /**
     * 校验SQL语法
     * 1. 定义一个简单的create语句（我们故意把name1的类型错误的设置成varchar2）
     * 2. 创建一个MySQL解析器实例
     * 3. 将sql语句传递给解析器
     * 4. 解析器开始检查语法
     * 5. 判断检查结果，0表示语法正确，1表示语法有错误，并获取返回的错误信息
     */
    @org.junit.Test
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



}
