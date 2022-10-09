package com.gsql.lineage;

import com.google.common.base.Supplier;

import java.util.Set;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.TCTEList;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import io.vavr.Function0;
import io.vavr.Tuple2;
import io.vavr.collection.List;

import static io.vavr.API.Tuple;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author hubin
 * @date 2022年09月30日 09:16
 */
public class Common {


    /**
     * 得到SQL的血缘,因为可以传入多个SQL语句已';'风格,所有返回为List.
     *
     * @param sql
     * @return Tuple._1 为 源表名Set, Tuple._2 为结果表名.
     */
    public static java.util.List<Tuple2<Set<String>, String>> getLineage(String sql) {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sql;
        assertTrue(sqlparser.parse() == 0);

        TStatementList sqlstatements = sqlparser.getSqlstatements();

        java.util.List<Tuple2<Set<String>, String>> resoult = List.ofAll(sqlstatements).map(tCustomSqlStatement -> {
            Set<String> tmpTables = List.<String>empty().toJavaSet();

            Set<String> sourceTableNames = getSourceTableNames(tCustomSqlStatement, tmpTables);
            // System.out.println("sourceTable: " + sourceTableNames);

            String targetTableName = getTargetTableName(tCustomSqlStatement);
            // System.out.println("targetTable: " + targetTableName);

            String sqlType = getSqlType(tCustomSqlStatement);
            // System.out.println("sqlType: " + sqlType);

            java.util.List<LineageSource> lineageSources = List.ofAll(sourceTableNames).map(x -> {
                LineageSource lineageSource = LineageSource.table(sqlType, x);
                return lineageSource;
            }).toJavaList();

            LineageTarget lineageTarget = LineageTarget.general(targetTableName);

            Lineage lineage = Lineage.relationship(lineageTarget, lineageSources);
            // System.out.println(lineage);

            return Tuple(sourceTableNames, targetTableName);
        }).toJavaList();
        return resoult;
    }


    /**
     * 获取statement的源表Set
     * 已知问题: all.开头的库名是不行的
     * @param stmt
     * @param outTmpTables 临时表的名称,用于结果过滤
     * @return
     */
    public static Set<String> getSourceTableNames(TCustomSqlStatement stmt, Set<String> outTmpTables) {

        Function0<Set<String>> cteTableFunc = () -> {
            if (stmt instanceof TSelectSqlStatement && ((TSelectSqlStatement) stmt).getCteList() != null) {

                TCTEList cteList = ((TSelectSqlStatement) stmt).getCteList();

                java.util.Set<String> cteTableNames = List.ofAll(cteList).map(x -> {
                    return x.getTableName().toString();
                }).toJavaSet();

                return cteTableNames;
            } else if (stmt instanceof TInsertSqlStatement && ((TInsertSqlStatement) stmt).getCteList() != null) {
                TCTEList cteList = ((TInsertSqlStatement) stmt).getCteList();

                java.util.Set<String> cteTableNames = List.ofAll(cteList).map(x -> {
                    return x.getTableName().toString();
                }).toJavaSet();

                return cteTableNames;

            } else {
                return List.<String>empty().toJavaSet();
            }
        };


        Function0<Set<String>> sourceTableFunc = () -> {
            if (stmt instanceof TSelectSqlStatement) {
                Set<String> tableNames = List.ofAll(((TSelectSqlStatement) stmt).getTables())
                        // 去掉内部表 'subquery'
                        .filter(x -> x.isBaseTable())
                        .map(x -> x.getTableName().toString()).toJavaSet();

                return tableNames;
            } else {
                return List.<String>empty().toJavaSet();
            }
        };

        Set<String> allSourceTable = sourceTableFunc.get();

        Set<String> tmpTables = cteTableFunc.get();
        Set<String> mergeTmpTables = List.of(tmpTables, outTmpTables).flatMap(x -> x).toJavaSet();

        List<String> outTables = List.ofAll(allSourceTable).removeAll(List.ofAll(mergeTmpTables));

        if (stmt.getStatements().size() == 0) {
            return List.ofAll(outTables).toJavaSet();
        } else {
            java.util.List<String> internalTables = List.ofAll(stmt.getStatements()).map(x -> getSourceTableNames(x, mergeTmpTables)).flatMap(x -> x).toJavaList();
            Set<String> totalTables = List.of(internalTables, outTables).flatMap(x -> x).toJavaSet();
            return totalTables;
        }
    }


    /**
     * 得到结果表的名称
     * @param stmt
     * @return
     */
    public static String getTargetTableName(TCustomSqlStatement stmt) {
        Function0<String> targetNameFunc = () -> {
            if (stmt.getTargetTable() != null) {
                return stmt.getTargetTable().getTableName().toString();
            } else {
                return "";
            }
        };

        return targetNameFunc.get();
    }

    public static String getSqlType(TCustomSqlStatement stmt) {
        Function0<String> getSqlTypeFunc = () -> {
            if (stmt instanceof TSelectSqlStatement) {
                return "select";
            } else if (stmt instanceof TInsertSqlStatement)
                return "insert";
            else if (stmt instanceof TCreateTableSqlStatement) {
                return "create";
            } else {
                return "other";
            }
        };

        return getSqlTypeFunc.get();
    }

}