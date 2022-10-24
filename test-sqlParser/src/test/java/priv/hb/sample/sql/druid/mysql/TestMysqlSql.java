package priv.hb.sample.sql.druid.mysql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;

import org.junit.Test;

import java.util.List;

/**
 * @author hubin
 * @date 2022年09月20日 14:26
 */
public class TestMysqlSql {
    @Test
    public void test1() {
        String sql =
                "select\n" +
                        "  id,\n" +
                        "  replace(trade_id, '\t', '') as trade_id,\n" +
                        "  type,\n" +
                        "  time_type,\n" +
                        "  limit_strategy_type,\n" +
                        "  replace(unique_key, '\t', '') as unique_key,\n" +
                        "  is_deleted,\n" +
                        "  replace(creator, '\t', '') as creator,\n" +
                        "  replace(editor, '\t', '') as editor,\n" +
                        "  replace(create_time, '\t', '') as create_time,\n" +
                        "  replace(edit_time, '\t', '') as edit_time\n" +
                        "from\n" +
                        "  t_limit_idempotent\n" +
                        "where\n" +
                        "  (\n" +
                        "    create_time >= '2022-05-12'\n" +
                        "    or edit_time >= '2022-05-12'\n" +
                        "  )\n" +
                        "  and 1 = 0";


        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
        System.out.println(sqlStatements);

        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatements.get(0);
        SQLSelect select = sqlSelectStatement.getSelect();
        MySqlSelectQueryBlock selectQuery = (MySqlSelectQueryBlock) select.getQuery();

        List<SQLSelectItem> selectList = selectQuery.getSelectList();
        // replace(trade_id, '	', '') AS trade_id
        SQLSelectItem sqlSelectItem = selectList.get(1);
        SQLMethodInvokeExpr expr = (SQLMethodInvokeExpr) sqlSelectItem.getExpr();
        // trade_id
        String alias = sqlSelectItem.getAlias();

        SQLTableSource from = selectQuery.getFrom();
        SQLBinaryOpExpr where = (SQLBinaryOpExpr) selectQuery.getWhere();

        // create_time >= '2022-05-12' OR edit_time >= '2022-05-12'
        SQLBinaryOpExpr left = (SQLBinaryOpExpr) where.getLeft();
        // 1 = 0
        SQLBinaryOpExpr right = (SQLBinaryOpExpr) where.getRight();
        // BooleanAnd
        SQLBinaryOperator operator = where.getOperator();


    }
}
