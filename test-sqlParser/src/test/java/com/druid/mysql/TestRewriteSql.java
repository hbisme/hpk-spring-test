package com.druid.mysql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.druid.common.Utils.cast;
import static com.druid.common.Utils.parseSQLSelectQuery;

/**
 * 修改sql语句测试
 *
 * @author hubin
 * @date 2022年09月21日 10:16
 */
public class TestRewriteSql {


    /**
     * 修改sql中的where表达式,再还原成sql语句
     */
    @Test
    public void modifyWhereExpr() {
        String sql = "select * from users where id = 1";
        SQLSelectQuery sqlSelectQuery = parseSQLSelectQuery(sql);
        MySqlSelectQueryBlock queryBlock = cast(sqlSelectQuery, MySqlSelectQueryBlock.class);
        SQLExpr where = queryBlock.getWhere();
        SQLBinaryOpExpr sqlBinaryOpExpr = cast(where, SQLBinaryOpExpr.class);

        sqlBinaryOpExpr.setLeft(SQLUtils.toSQLExpr("name"));
        sqlBinaryOpExpr.setRight(SQLUtils.toSQLExpr("hb"));


        String res = SQLUtils.toSQLString(sqlSelectQuery);
        System.out.println(res);
    }


    /**
     * 修改sql中的select 字段, 再还原成sql语句
     */
    @Test
    public void modifySelectExpr() {
        String sql = "select id,name from users where id = 1";
        SQLSelectQuery sqlSelectQuery = parseSQLSelectQuery(sql);
        MySqlSelectQueryBlock queryBlock = cast(sqlSelectQuery, MySqlSelectQueryBlock.class);
        List<SQLSelectItem> selectList = queryBlock.getSelectList();

        for (SQLSelectItem sqlSelectItem : selectList) {
            if (sqlSelectItem.getExpr().toString().equals("id")) {
                sqlSelectItem.setExpr(SQLUtils.toSQLExpr("iid"));
            }
        }
        String res = SQLUtils.toSQLString(sqlSelectQuery);

        System.out.println(res);
    }

    /**
     * 修改sql中的from表 字段, 再还原成sql语句
     */
    @Test
    public void modifyFromExpr() {
        String sql = "select * from user1 where id = 1";
        SQLSelectQuery sqlSelectQuery = parseSQLSelectQuery(sql);
        MySqlSelectQueryBlock queryBlock = cast(sqlSelectQuery, MySqlSelectQueryBlock.class);
        SQLTableSource from = queryBlock.getFrom();
        SQLExprTableSource source = cast(from, SQLExprTableSource.class);
        source.setExpr(SQLUtils.toSQLExpr("table2"));

        String res = SQLUtils.toSQLString(sqlSelectQuery);

        System.out.println(res);
    }


    /**
     * 修改limit
     */
    @Test
    public void modifyLimit() {
        String sql1 = "select * from user1 where id = 1 limit 10000";
        SQLSelectQuery sqlSelectQuery = parseSQLSelectQuery(sql1);
        MySqlSelectQueryBlock queryBlock = cast(sqlSelectQuery, MySqlSelectQueryBlock.class);
        SQLLimit limit = queryBlock.getLimit();
        limit.setRowCount(100);

        String res = SQLUtils.toSQLString(sqlSelectQuery);
        System.out.println(res);


        String sql2 = "select * from user1 where id = 2";
        SQLSelectQuery sqlSelectQuery2 = parseSQLSelectQuery(sql2);
        MySqlSelectQueryBlock queryBlock2 = cast(sqlSelectQuery2, MySqlSelectQueryBlock.class);
        SQLLimit sqlLimit = new SQLLimit();
        sqlLimit.setRowCount(100);
        queryBlock2.setLimit(sqlLimit);
        String res2 = SQLUtils.toSQLString(sqlSelectQuery);
        System.out.println(res2);



        String sql3 = "select * from user1 where id = 2 limit 10, 10000";
        SQLSelectQuery sqlSelectQuery3 = parseSQLSelectQuery(sql3);
        MySqlSelectQueryBlock queryBlock3 = cast(sqlSelectQuery3, MySqlSelectQueryBlock.class);
        SQLLimit limit3 = queryBlock3.getLimit();
        limit3.setRowCount(100);


        String res3 = SQLUtils.toSQLString(sqlSelectQuery3);
        System.out.println(res3);




    }


}
