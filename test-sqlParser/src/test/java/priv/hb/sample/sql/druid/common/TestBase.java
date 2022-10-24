package priv.hb.sample.sql.druid.common;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static priv.hb.sample.sql.druid.common.Utils.parseSQLSelectQuery;

/**
 * @author hubin
 * @date 2022年09月20日 19:39
 */
@Slf4j
public class TestBase {
    @Test
    public void formatSql() {
        String mysqlSql1 = "SELECT *,CASE WHEN UNIX_TIMESTAMP( expire_time ) < UNIX_TIMESTAMP( NOW( ) ) THEN 1 ELSE 0 END state FROM `expire_time_data`;";
        String format1 = SQLUtils.format(mysqlSql1, DbType.mysql);
        System.out.println(format1);

        System.out.println("--------\n");
        String hiveSql1 =
                "-- 文件存储形式是parquet\n" +
                        "CREATE EXTERNAL TABLE IF NOT EXISTS default.person_table( \n" +
                        "ftpurl        string, \n" +
                        "ipcid         string, \n" +
                        "feature       array<float>, \n" +
                        "    eyeglasses    int, \n" +
                        "    timeslot      int, \n" +
                        "    exacttime     Timestamp, \n" +
                        "    searchtype    string, \n" +
                        "    sharpness     int\n" +
                        ") \n" +
                        "partitioned by (date string) \n" +
                        "STORED AS PARQUET \n" +
                        "LOCATION '/user/hive/warehouse/person_table';\n";
        String format2 = SQLUtils.format(hiveSql1, DbType.hive);
        System.out.println(format2);


    }


    @Test
    public void statementType() {
        // 以下全部 true
        System.out.println(SQLUtils.parseSingleMysqlStatement("select * from users") instanceof SQLSelectStatement);
        System.out.println(SQLUtils.parseSingleMysqlStatement("insert into users(id,name,age) values (1,'孙悟空',500)") instanceof SQLInsertStatement);
        System.out.println(SQLUtils.parseSingleMysqlStatement("update users set name = '唐僧' where id = 1 ") instanceof SQLUpdateStatement);
        System.out.println(SQLUtils.parseSingleMysqlStatement("delete from users where id = 1") instanceof SQLDeleteStatement);

    }

    @Test
    public void SQLSelectQuery() {
        System.out.println(parseSQLSelectQuery("select * from users") instanceof SQLSelectQueryBlock);

        System.out.println(parseSQLSelectQuery("select name from users union select name from school") instanceof SQLUnionQuery);
    }


    /**
     * 操作符相关: SQLBinaryOpExpr
     */
    @Test
    public void SQLBinaryOpExpr() {
        String sql = "select * from users where id > 1 and age = 18";
        SQLSelectQuery sqlSelectQuery = parseSQLSelectQuery(sql);
        SQLSelectQueryBlock selectQueryBlock = Utils.cast(sqlSelectQuery, SQLSelectQueryBlock.class);
        SQLExpr where = selectQueryBlock.getWhere();
        List<SQLObject> conditions = where.getChildren();
        for (SQLObject condition : conditions) {
            SQLBinaryOpExpr conditionExpr = Utils.cast(condition, SQLBinaryOpExpr.class);
            SQLBinaryOperator operator = conditionExpr.getOperator();
            SQLIdentifierExpr conditionColumn = Utils.cast(conditionExpr.getLeft(), SQLIdentifierExpr.class);
            SQLValuableExpr conditionColumnValue = Utils.cast(conditionExpr.getRight(), SQLValuableExpr.class);
            log.info("条件字段:{},操作符号:{},条件值:{}", conditionColumn.getName(), operator.name, conditionColumnValue);

        }


        System.out.println();
    }


    @Test
    public void SQLVariantRefExpr() {
        String sql = "select * from users where id = ? and name = ?";
        SQLSelectQuery sqlSelectQuery = Utils.parseSQLSelectQuery(sql);
        SQLSelectQueryBlock selectQueryBlock = Utils.cast(sqlSelectQuery, SQLSelectQueryBlock.class);
        SQLExpr where = selectQueryBlock.getWhere();
        List<SQLObject> conditions = where.getChildren();

        // [id = ?] 出现了变量符,所以要用SQLVariantRefExpr
        for (SQLObject condition : conditions) {
            SQLBinaryOpExpr conditionExpr = Utils.cast(condition, SQLBinaryOpExpr.class);
            SQLBinaryOperator operator = conditionExpr.getOperator();
            SQLIdentifierExpr conditionColumn = Utils.cast(conditionExpr.getLeft(), SQLIdentifierExpr.class);
            SQLVariantRefExpr conditionColumnValue = Utils.cast(conditionExpr.getRight(), SQLVariantRefExpr.class);
            int index = conditionColumnValue.getIndex();
            log.info("条件字段:{},操作符号:{},索引位:{}", conditionColumn.getName(), operator.name, index);
        }

    }

    @SneakyThrows
    @ParameterizedTest(name = "case-{index}")
    @CsvSource(value = {
            "select * from emp where i = 3 | com.alibaba.druid.sql.ast.statement.SQLExprTableSource | true",
            "select * from emp e inner join org o on e.org_id = o.id | com.alibaba.druid.sql.ast.statement.SQLJoinTableSource | true",
            "select * from (select * from temp) a | com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource |true"
    }, delimiter = '|')
    public void testTableSource(String sql, String clazzName, String tmp) {
        // String sql = "select * from emp where i = 3";
        SQLSelectQuery sqlSelectQuery = Utils.parseSQLSelectQuery(sql);
        MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) sqlSelectQuery;
        Class<?> clazz = Class.forName(clazzName);
        Object cast = clazz.cast(query.getFrom());

        System.out.println(cast.getClass());
    }


}
