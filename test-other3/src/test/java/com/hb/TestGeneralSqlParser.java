package com.hb;

import org.junit.Test;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;

/**
 * 第三方的SQL解析器
 * @author hubin
 * @date 2022年08月04日 14:38
 */
public class TestGeneralSqlParser {

    /**
     * 校验SQL语法
     * 1. 定义一个简单的create语句（我们故意把name1的类型错误的设置成varchar2）
     * 2. 创建一个MySQL解析器实例
     * 3. 将sql语句传递给解析器
     * 4. 解析器开始检查语法
     * 5. 判断检查结果，0表示语法正确，1表示语法有错误，并获取返回的错误信息
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
     * 提取多条SQL
     */
    @Test
    public void testStatement() {
        String sql = " -- 多条SQL语句; "
                + "alter table tab1 add (author_last_published2 date); -- 添加字段\n"//添加1个字段
                + " alter table tab2 add (author_last_published3 date, age number(10,2)); -- 添加两个字段\n"//添加多个字段
                + "drop table tab3; -- 删除表; \n";    //删表;

        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlParser.sqltext = sql;
        sqlParser.parse();

        TStatementList stList = sqlParser.sqlstatements;
        System.out.println("共有条"+stList.size()+"sql语句!");
        for(int i=0; i<stList.size(); i++){
            TCustomSqlStatement customSt = stList.get(i);
            String tsql = customSt.toString();
            System.out.println("第"+(i+1)+"条sql:"+tsql);
        }




    }



}
