package com.gsql;

import org.junit.jupiter.api.Test;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.nodes.hive.THiveHintClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import junit.framework.TestCase;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author hubin
 * @date 2022年09月23日 09:54
 */
public class TestJoin {
    @Test
    public void testJoin() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext =
                "SELECT page_views.*\n" +
                        "FROM page_views JOIN dim_users\n" +
                        "  ON (page_views.user_id = dim_users.id " +
                        "      AND page_views.date >= '2008-03-01' " +
                        "      AND page_views.date <= '2008-03-31')";

        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(select.getResultColumnList().size() == 1);

        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_table);
        assertTrue(join.getTable().toString().equalsIgnoreCase("page_views"));
        TJoinItem joinItem = join.getJoinItems().getJoinItem(0);
        assertTrue(joinItem.getJoinType() == EJoinType.join);
        assertTrue(joinItem.getTable().toString().equalsIgnoreCase("dim_users"));
        TExpression joinCondition = joinItem.getOnCondition();
        assertTrue(joinCondition.getExpressionType() == EExpressionType.parenthesis_t);
        joinCondition = joinCondition.getLeftOperand();
        assertTrue(joinCondition.getExpressionType() == EExpressionType.logical_and_t);
        //System.out.println(joinCondition.toString());
    }

}
