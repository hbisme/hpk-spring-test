package com.druid.common;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hubin
 * @date 2022年09月20日 19:48
 */
@Slf4j
public class Utils {
    public static SQLSelectQuery parseSQLSelectQuery(String sql) {
        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
        SQLSelect select = sqlSelectStatement.getSelect();
        return select.getQuery();
    }

    public static <T, U> U cast(T t, Class<U> u) {
        return u.cast(t);
    }


    /**
     * 解析查询字段,注意是否使用了别名.u.id as userId, u.name as userName, u.age as userAge
     * userId(sqlSelectItem.getAlias)
     * 如果有别名: u.id( id = SQLPropertyExpr.getName,u = SQLPropertyExpr.getOwnernName)
     * 如果没别名: id(id = SQLIdentifierExpr.name)
     *
     * @param selectColumnList 查询字段
     */
    private void parseSQLSelectItem(List<SQLSelectItem> selectColumnList) {
        for (SQLSelectItem sqlSelectItem : selectColumnList) {
            // u.id as userId  (selectColumnAlias)
            String selectColumnAlias = sqlSelectItem.getAlias();

            // u.id = SQLPropertyExpr
            SQLExpr expr = sqlSelectItem.getExpr();
            if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr selectColumnExpr = cast(expr, SQLPropertyExpr.class);
                log.info("列名:{}, 别名:{}, 表别名:{}", selectColumnExpr.getName(), selectColumnAlias, selectColumnExpr.getOwnernName());
            }

            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr selectColumnExpr = cast(expr, SQLIdentifierExpr.class);
                log.info("列名:{},别名:{}", selectColumnExpr.getName(), selectColumnAlias);
            }


        }

    }


}
