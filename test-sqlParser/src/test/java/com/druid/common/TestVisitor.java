package com.druid.common;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.druid.common.visitor.CustomerHiveSqlASTVisitorAdapter;
import com.druid.common.visitor.CustomerMySqlASTVisitorAdapter;

import org.junit.Test;

/**
 * @author hubin
 * @date 2022年09月21日 11:08
 */
public class TestVisitor {
    @Test
    public void test1() {
        String sql = "select u.id as userId, u.name as userName, age  from users as u where u.id = 1 and u.name = '孙悟空' limit 2,10";
        // 解析SQL
        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        CustomerMySqlASTVisitorAdapter customerMySqlASTVisitorAdapter = new CustomerMySqlASTVisitorAdapter();
        sqlStatement.accept(customerMySqlASTVisitorAdapter);
        // 表别名:{u=users}
        System.out.println("表别名:" + customerMySqlASTVisitorAdapter.getAliasMap());
        // 列别名{userName=u.name, userId=u.id, userAge=age}
        System.out.println("列别名" + customerMySqlASTVisitorAdapter.getAliasColumnMap());

    }


    @Test
    public void test2() {
        String sql = "select u.id as userId, u.name as userName, age  from users as u where u.id = 1 and u.name = '孙悟空' limit 1";
        // 解析SQL
        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        CustomerHiveSqlASTVisitorAdapter customerMySqlASTVisitorAdapter = new CustomerHiveSqlASTVisitorAdapter();
        sqlStatement.accept(customerMySqlASTVisitorAdapter);
        // 表别名:{u=users}
        System.out.println("表别名:" + customerMySqlASTVisitorAdapter.getAliasMap());
        // 列别名{userName=u.name, userId=u.id, userAge=age}
        System.out.println("列别名" + customerMySqlASTVisitorAdapter.getAliasColumnMap());


        String res = SQLUtils.toSQLString(sqlStatement);
        System.out.println(res);

    }
}
