package priv.hb.sample.sql.gsql.utils;

import priv.hb.sample.sql.gsql.lineage.Lineage;
import priv.hb.sample.sql.gsql.lineage.LineageSource;
import priv.hb.sample.sql.gsql.lineage.LineageTarget;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.TSyntaxError;
import gudusoft.gsqlparser.nodes.TCTEList;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TExpressionList;
import gudusoft.gsqlparser.nodes.TObjectName;
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
 * @date 2022年10月09日 20:07
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

            Set<String> sourceTableNames = getSourceTableNamesFunc(tCustomSqlStatement, tmpTables);
            // System.out.println("sourceTable: " + sourceTableNames);

            String targetTableName = getTargetTableNameFunc(tCustomSqlStatement);
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
     *
     * @param stmt
     * @param outTmpTables 临时表的名称,用于结果过滤
     * @return
     */
    public static Set<String> getSourceTableNamesFunc(TCustomSqlStatement stmt, Set<String> outTmpTables) {

        Function0<Set<String>> cteTableFunc = () -> {
            if (stmt instanceof TSelectSqlStatement && ((TSelectSqlStatement) stmt).getCteList() != null) {

                TCTEList cteList = ((TSelectSqlStatement) stmt).getCteList();

                java.util.Set<String> cteTableNames = io.vavr.collection.List.ofAll(cteList).map(x -> {
                    return x.getTableName().toString();
                }).toJavaSet();

                return cteTableNames;
            } else if (stmt instanceof TInsertSqlStatement && ((TInsertSqlStatement) stmt).getCteList() != null) {
                TCTEList cteList = ((TInsertSqlStatement) stmt).getCteList();

                java.util.Set<String> cteTableNames = io.vavr.collection.List.ofAll(cteList).map(x -> {
                    return x.getTableName().toString();
                }).toJavaSet();

                return cteTableNames;

            } else {
                return io.vavr.collection.List.<String>empty().toJavaSet();
            }
        };


        Function0<Set<String>> sourceTableFunc = () -> {
            if (stmt instanceof TSelectSqlStatement) {
                Set<String> tableNames = io.vavr.collection.List.ofAll(((TSelectSqlStatement) stmt).getTables())
                        // 去掉内部表 'subquery'
                        .filter(x -> x.isBaseTable())
                        .map(x -> x.getTableName().toString()).toJavaSet();

                return tableNames;
            } else {
                return io.vavr.collection.List.<String>empty().toJavaSet();
            }
        };

        Set<String> allSourceTable = sourceTableFunc.get();

        Set<String> tmpTables = cteTableFunc.get();
        Set<String> mergeTmpTables = io.vavr.collection.List.of(tmpTables, outTmpTables).flatMap(x -> x).toJavaSet();

        io.vavr.collection.List<String> outTables = io.vavr.collection.List.ofAll(allSourceTable).removeAll(io.vavr.collection.List.ofAll(mergeTmpTables));

        if (stmt.getStatements().size() == 0) {
            return io.vavr.collection.List.ofAll(outTables).toJavaSet();
        } else {
            java.util.List<String> internalTables = io.vavr.collection.List.ofAll(stmt.getStatements()).map(x -> getSourceTableNamesFunc(x, mergeTmpTables)).flatMap(x -> x).toJavaList();
            Set<String> totalTables = io.vavr.collection.List.of(internalTables, outTables).flatMap(x -> x).toJavaSet();
            return totalTables;
        }
    }

    /**
     * 得到结果表的名称
     *
     * @param stmt
     * @return
     */
    public static String getTargetTableNameFunc(TCustomSqlStatement stmt) {
        Function0<String> targetNameFunc = () -> {
            if (stmt.getTargetTable() != null) {
                return stmt.getTargetTable().getTableName().toString();
            } else {
                return "";
            }
        };

        return targetNameFunc.get();
    }

    /**
     * 获取静态分区信息
     *
     * @param stmt
     * @return
     */
    public static java.util.List<Tuple2<String, String>> getStaticPartition(TCustomSqlStatement stmt) {

        Function0<java.util.List<Tuple2<String, String>>> partitionFunc = () -> {

            if (stmt.getTargetTable() == null) {
                return List.<Tuple2<String, String>>empty().toJavaList();
            }

            if (stmt.getTargetTable().getPartitionExtensionClause() == null) {
                return List.<Tuple2<String, String>>empty().toJavaList();
            }

            TExpressionList keyValues = stmt.getTargetTable().getPartitionExtensionClause().getKeyValues();

            java.util.List<Tuple2<String, String>> tuple2s = List.ofAll(keyValues).filter(x -> x.getLeftOperand() != null).map(x -> {
                String key = x.getLeftOperand().getPlainText();
                String value = x.getRightOperand().getPlainText();
                return Tuple(key, value);
            }).toJavaList();
            return tuple2s;
        };
        return partitionFunc.get();
    }


    /**
     * 获取动态分区信息
     *
     * @param stmt
     * @return
     */
    public static java.util.List<String> getDynamicPartition(TCustomSqlStatement stmt) {

        Function0<java.util.List<String>> partitionFunc = () -> {

            if (stmt.getTargetTable() == null) {
                return List.<String>empty().toJavaList();
            }

            if (stmt.getTargetTable().getPartitionExtensionClause() == null) {
                return List.<String>empty().toJavaList();
            }

            TExpressionList keyValues = stmt.getTargetTable().getPartitionExtensionClause().getKeyValues();

            java.util.List<String> tuple2s = List.ofAll(keyValues).filter(x -> x.getLeftOperand() == null).map(x -> {
                String value = x.getObjectOperand().getPlainText();
                return value;
            }).toJavaList();
            return tuple2s;
        };
        return partitionFunc.get();
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


    /**
     * 去掉sql中的注释,很强大!
     *
     * @param sql
     * @return
     */
    public static String removeComments(String sql) {
        Pattern p = Pattern.compile("(?ms)('(?:''|[^'])*')|--.*?$|/\\*.*?\\*/|#.*?$|");
        String presult = p.matcher(sql).replaceAll("$1");
        return presult;
    }

    /**
     * 得到sql中的错误信息
     *
     * @param sqlParser
     * @return
     */
    public static java.util.List<String> getErrors(TGSqlParser sqlParser) {
        ArrayList<TSyntaxError> syntaxErrors = sqlParser.getSyntaxErrors();
        ArrayList<String> errors = new ArrayList<>();
        errors.add(sqlParser.getErrormessage());
        for (TSyntaxError syntaxError : syntaxErrors) {
            errors.add(String.format("lineNo: %s, columnNo: %s, hint: %s, tokentext: %s", syntaxError.lineNo, syntaxError.columnNo, syntaxError.hint, syntaxError.tokentext));
        }
        return errors;
    }

    public static java.util.List<String> splitSql(String sqls) {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sqls;
        assertTrue(sqlparser.parse() == 0);

        TStatementList sqlstatements = sqlparser.getSqlstatements();

        java.util.List<String> result = List.ofAll(sqlstatements).map(sqlStatement -> {
            return sqlStatement.getPlainText();
        }).toJavaList();

        return result;
    }


    /**
     * 得到表达式中有哪些字段
     * @param expression
     * @return
     */
    public static java.util.List<String> getTExpressionFunc(TExpression expression) {
        TExpression left = expression.getLeftOperand();
        TExpression right = expression.getRightOperand();
        TObjectName objectOperand = expression.getObjectOperand();

        if (left == null && right == null) {
            return List.of(objectOperand.getPlainText()).toJavaList();
        }

        java.util.List<String> tExpressionFunc = getTExpressionFunc(left);
        java.util.List<String> tExpressionFunc1 = getTExpressionFunc(right);
        return List.of(tExpressionFunc, tExpressionFunc1).flatMap(x -> x).toJavaList();
    }
}
