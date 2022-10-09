package com.druid.common;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUseStatement;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.vavr.Tuple;
import io.vavr.Tuple2;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUseStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yt.asd.kit.validate.YtValidateUtil;


import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import lombok.extern.slf4j.Slf4j;

/**
 * @author hubin
 * @date 2022年09月19日 16:47
 */
@Slf4j
public class SqlAnalyze {
    /**
     * 从 creaet table/view 中解析读取/写入的数据表
     * case:
     * - create view AS select ...
     *
     * @param ddlSql
     * @return srcTableList, targetTable
     */
    public static Tuple2<List<String>, String> extractTableFromCreateSql(String ddlSql) {
        ddlSql = ddlSql.trim().toLowerCase();

        List<SQLStatement> statementList = SQLUtils.parseStatements(ddlSql, JdbcConstants.HIVE);
        SQLStatement sqlStatement = statementList.get(0);
        if (sqlStatement instanceof SQLCreateTableStatement) {
            SQLCreateTableStatement sqlCreateTableStmt = (SQLCreateTableStatement) sqlStatement;

            String toTable = sqlCreateTableStmt.getTableSource().getTableName();
            return Tuple.of(null, toTable);
        }

        /*
         * 创建 view 的场景
         */
        if (sqlStatement instanceof SQLCreateViewStatement) {
            SQLCreateViewStatement sqlCreateViewStmt = (SQLCreateViewStatement) sqlStatement;

            String toTable = sqlCreateViewStmt.getTableSource().getName().toString();

            Set<String> fromTableSet = extractFromTables(ddlSql);

            return Tuple.of(Lists.newArrayList(fromTableSet), toTable);
        }

        throw new UnsupportedOperationException("仅支持 create-table , create-view语句");
    }


    public static Set<String> extractFromTables(String sql) throws ParserException {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        if (stmts == null) {
            return null;
        }

        Set<String> fromSet = Sets.newHashSet();

        String database = null;
        for (SQLStatement stmt : stmts) {
            SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
            if (stmt instanceof SQLUseStatement) {
                database = ((SQLUseStatement) stmt).getDatabase().getSimpleName();
            }
            stmt.accept(statVisitor);

            Map<TableStat.Name, TableStat> tables = statVisitor.getTables();
            if (tables == null) {
                continue;
            }

            final String db = database;

            tables.forEach((tableName, stat) -> {
                if (stat.getSelectCount() <= 0) {
                    return;
                }

                String from = tableName.getName();
                if (db != null && !from.contains(".")) {
                    from = db + "." + from;
                }
                fromSet.add(from);
            });
        }

        return fromSet;

    }


    /**
     * 从 with 查询SQL中解析读取/写入的数据表
     * case:
     * - with t_a AS ( select ...), t_b AS ( select ...) insert overwrite table X ...
     *
     * @param withSelectSql
     * @return srcTableList, targetTable
     */
    public static Tuple2<List<String>, String> extractTableFromWithQuery(String withSelectSql) {
        withSelectSql = withSelectSql.trim().toLowerCase();

        /**
         * 处理异常case-1： INSERT overwrite directory '/tmp/dim_hpc_itm_item_temp' select ...
         */
        boolean writeDirectory = false;
        String targetDirStr = StringUtils.substringBetween(withSelectSql, "insert overwrite directory", "select");
        if (StringUtils.isNotBlank(targetDirStr)) {
            writeDirectory = true;
            withSelectSql = StringUtils.replace(withSelectSql, "insert overwrite directory", "insert overwrite table");
        }

        // 处理异常 case-2: druid-1.2.x 不支持 `all`作为字段前缀
        withSelectSql = StringUtils.replace(withSelectSql, " all.", " a.");
        List<SQLStatement> statementList = SQLUtils.parseStatements(withSelectSql, JdbcConstants.HIVE);
        SQLStatement sqlStatement = statementList.get(0);

        /*
         * 解析 with-select-insert 语句，eg: with t1 as (select ...), t2 as (select ..) insert overwrite table T select ...
         */
        // 1、提取出 SQL前半部分-with 查询对应的SQL语句，eg: with t1 as (select ...), t2 as (select ..)

        boolean hasNoInsert = false;
        SQLWithSubqueryClause with = null;
        StringBuilder withSelect = new StringBuilder();

        // 异常case-3：仅有 with-select 部分，缺少 insert 部分
        if (sqlStatement instanceof SQLSelectStatement) {
            hasNoInsert = true;

            SQLSelectStatement hiveSelectStmt = (SQLSelectStatement) sqlStatement;
            SQLSelect select = hiveSelectStmt.getSelect();
            with = select.getWithSubQuery();

            String mainSelectSql = select.getQuery().toString();
            withSelect.append(mainSelectSql).append(";");

        } else {
            HiveInsertStatement hiveInsertStmt = (HiveInsertStatement) sqlStatement;
            with = hiveInsertStmt.getWith();
        }

        // 从 with A as ( select-sql ) 语句中提取 select-sql 部分
        List<SQLWithSubqueryClause.Entry> entries = with.getEntries();
        Set<String> withTableSet = Sets.newHashSetWithExpectedSize(entries.size());
        for (SQLWithSubqueryClause.Entry entry : entries) {
            String withTableName = entry.getAlias();
            withTableSet.add(withTableName);

            // 异常case：insert ytdw.hive_info
            if (!withTableName.contains(".")) {
                withTableSet.add("ytdw." + withTableName);
            }

            SQLSelect subQuery = entry.getSubQuery();
            // 处理异常case-2：对 druid SQL 解析 bug 进行处理
            String tmpWithSelectSql = subQuery.toString();
            if (StringUtils.containsIgnoreCase(tmpWithSelectSql, "item_idSORT")) {
                tmpWithSelectSql = StringUtils.replace(tmpWithSelectSql, "item_idSORT", "item_id sort");
            }
            withSelect.append(tmpWithSelectSql).append(";");
        }

        Set<String> withSelectTableSet = extractFromTables(withSelect.toString());
        log.info("Extraced tables={} from with-select part, totalTblCnt={}", withSelectTableSet, withSelectTableSet.size());


        if (hasNoInsert) {
            Sets.SetView<String> difference1 = Sets.difference(withSelectTableSet, withTableSet);
            Set<String> finalWithSelectTables = difference1.stream().collect(Collectors.toSet());
            return Tuple.of(Lists.newArrayList(finalWithSelectTables), null);
        }

        // 2、提取出 SQL后半部分-insert 查询对应的SQL，eg：insert overwrite table T select ...
        HiveInsertStatement hiveInsertStmt = (HiveInsertStatement) sqlStatement;
        Set<String> insertSelectTableSet = extractFromTables(hiveInsertStmt.getQuery().toString());
        log.info("Extraced tables={} from insert-select part, totalTblCnt={}", insertSelectTableSet, insertSelectTableSet.size());

        // 【注意】剔除 with 产生的 t1、t2 等非物理存在的中间表
        Sets.SetView<String> difference1 = Sets.difference(withSelectTableSet, withTableSet);
        Sets.SetView<String> difference2 = Sets.difference(insertSelectTableSet, withTableSet);

        Set<String> finalWithSelectTables = difference1.stream().collect(Collectors.toSet());
        Set<String> finalInsertSelectTables = difference2.stream().collect(Collectors.toSet());

        finalWithSelectTables.addAll(finalInsertSelectTables);
        List<String> tables = Lists.newArrayList(finalWithSelectTables);
        log.info("Extraced final  tables={} totalTblCnt={}", tables, tables.size());

        // 3、提取出 insert 的目标数据表
        String toTable = extractTableName(hiveInsertStmt.getTableSource());

        // 处理异常case-1，写入目录时不产生目标数据表，eg: INSERT overwrite directory '/tmp/dim_hpc_itm_item_temp' select ...
        toTable = writeDirectory ? null : toTable;

        return Tuple.of(Lists.newArrayList(finalWithSelectTables), toTable);
    }

    private static String extractTableName(SQLExprTableSource tableSource) {
        SQLExpr tableExpr = tableSource.getExpr();

        // 表名中带有库名，如：db.table
        if (tableExpr instanceof SQLPropertyExpr) {
            SQLPropertyExpr tableWithDbExpr = (SQLPropertyExpr) tableExpr;
            return tableWithDbExpr.getOwner() + "." + tableWithDbExpr.getName();
        }
        return tableSource.getName().getSimpleName();
    }

    public static SQLSelectStatement parseSql(String sql) {
        SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, JdbcConstants.HIVE, false);
        YtValidateUtil.isTrue(sqlStatement instanceof SQLSelectStatement, "Input [sql] must be select query");

        return (SQLSelectStatement) sqlStatement;
    }

    /**
     * 检查 select-SQL 语句的 where 条件中是否仅包含指定字段
     *
     * @param sqlSelectStmt
     * @param columns
     * @return
     */
    public static boolean hasExactlyColumnsInWhereCondition(SQLSelectStatement sqlSelectStmt, List<String> columns) {
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        sqlSelectStmt.accept(visitor);
        List<TableStat.Condition> conditions = visitor.getConditions();
        Set<String> whereColumnSet = conditions.stream().map(condition -> {
            return condition.getColumn().getName();
        }).collect(Collectors.toSet());

        // select-where 中提取出字段，与给定的字段是否完全相同
        Sets.SetView<String> difference = Sets.difference(whereColumnSet, Sets.newHashSet(columns));
        return difference.size() == 0;
    }

    /**
     * 检查 select-SQL 语句的 where 有多个条件
     *
     * @param sqlSelectStmt
     * @return
     */
    public static boolean hasMultiWhereCondition(SQLSelectStatement sqlSelectStmt) {
        if (sqlSelectStmt.getSelect() == null) {
            return true;
        }

        if (!(sqlSelectStmt.getSelect().getQuery() instanceof SQLSelectQueryBlock)) {
            return false;
        }
        SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlSelectStmt.getSelect().getQuery();
        if (sqlSelectQueryBlock.getWhere() == null) {
            return false;
        }

        if (!(sqlSelectQueryBlock.getWhere() instanceof SQLBinaryOpExpr)) {
            return false;
        }
        SQLBinaryOpExpr sqlExpr = (SQLBinaryOpExpr)sqlSelectQueryBlock.getWhere();
        if (sqlExpr.getLeft() != null && sqlExpr.getLeft() instanceof SQLBinaryOpExpr
                && sqlExpr.getRight() != null && sqlExpr.getRight() instanceof SQLBinaryOpExpr) {
            return true;
        }
        return false;
    }



}
