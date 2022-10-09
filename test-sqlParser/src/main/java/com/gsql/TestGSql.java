package com.gsql;

import java.util.LinkedList;
import java.util.stream.StreamSupport;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.dlineage.DataFlowAnalyzer;
// import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.util.RemoveDataflowFunction;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class TestGSql {
    public static void main(String[] args) {

        String sql1 = "CREATE TABLE tmp.jinpushi_066\n" +
                "AS\n" +
                "SELECT \n" +
                "T1.a1, " +
                "T1.a2, " +
                "T1.a3, " +
                "T1.a4 " +
                ",T2.b1" +
                ",T2.b2 \n" +
                ",T3.b3\n" +
                ",T2.b4\n" +
                ",T1.a5\n" +
                "FROM dwd.jinpushi_01 T1\n" +
                "LEFT JOIN dim.jinpushi_02 T2\n" +
                "ON T1.id = T2.id\n" +
                "LEFT JOIN ods.jinpushi_03 T3\n" +
                "ON T1.code = T3.value\n" +
                "AND T3.tyoe = 12306\n" +
                ";\n" +

                "INSERT OVERWRITE TABLE dwb.jinpushi_05 \n" +
                "SELECT * FROM tmp.jinpushi_066\n" +
                ";";

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sql1;
        int ret = sqlparser.parse();
        if (ret == 0) {
            // 解析出所有语句
            TStatementList stmts = sqlparser.getSqlstatements();

            // 拿到create table语句的实例
            TCreateTableSqlStatement stmt = (TCreateTableSqlStatement) stmts.get(0);

            // 从create table语句的子查询中，拿到select语句的实例，再获取column
            TSelectSqlStatement subquery = stmt.getSubQuery();
            TResultColumnList columns = subquery.getResultColumnList();
            LinkedList<String>[] lineages = new LinkedList[columns.size()];

            for (int i = 0; i < columns.size(); i++) {
                TResultColumn column = columns.getResultColumn(i);
                LinkedList<String> lineage = lineages[i] = new LinkedList<>();
                lineage.addFirst(String.format("%s(%s)", column.getDisplayName(), stmt.getTableName()));
                lineage.addFirst(String.format("%s(RS-1)", column.getDisplayName()));

                String columnName = "";
                if (column.getExpr() != null) {
                    TExpression expr = column.getExpr();
                    while (expr.getFunctionCall() != null) {
                        expr = expr.getFunctionCall().getArgs().getExpression(0);
                    }
                    columnName = expr.toString();
                }

                String[] pair = columnName.split("\\.");
                if (pair.length == 2) {
                    // 有alias，在alias对应的select语句中搜索
                    String prefix = pair[0];
                    String columnDisplayName = pair[1];
                    lineage.addFirst(String.format("%s(%s)", columnDisplayName, prefix));
                    StreamSupport
                            .stream(subquery.tables.spliterator(), false)
                            .filter(t -> t.getAliasClause().toString().equalsIgnoreCase(prefix))
                            .findFirst().ifPresent(table -> {
                                TSelectSqlStatement subquery1 = table.subquery;
                                if (subquery1 != null) {
                                    TResultColumnList resultColumnList = subquery1.getResultColumnList();
                                    resultColumnList.forEach(tableColumn -> {
                                        if (columnDisplayName.equalsIgnoreCase(tableColumn.getDisplayName())) {
                                            if (tableColumn.getExpr().getFunctionCall() == null) {
                                                lineage.addFirst(String.format("%s(%s)", columnDisplayName, table.subquery.tables.getTable(0).getTableName()));
                                            } else {
                                                lineage.addFirst(String.format("%s(%s)",
                                                        tableColumn.getExpr().getFunctionCall().getArgs().getElement(0),
                                                        table.subquery.tables.getTable(0).getTableName()));
                                            }
                                        }
                                    });
                                }
                            });
                } else if (pair.length == 1) {
                    // 没有alias，在所有的select语句中搜索
                    String columnDisplayName = pair[0];
                    StreamSupport
                            .stream(subquery.tables.spliterator(), false)
                            .filter(t -> {
                                if (t.subquery != null) {
                                    for (int j = 0; j < t.subquery.getResultColumnList().size(); j++) {
                                        if (t.subquery.getResultColumnList().getResultColumn(j).getDisplayName().equalsIgnoreCase(columnDisplayName)) {
                                            return true;
                                        }
                                    }
                                }
                                return false;
                            })
                            .findFirst().ifPresent(table -> {
                                lineage.addFirst(String.format("%s(%s)", columnDisplayName, table.getAliasClause()));
                                lineage.addFirst(String.format("%s(%s)", columnDisplayName, table.subquery.tables.getTable(0)));
                            });
                }
            }

            for (LinkedList<String> lineage : lineages) {
                System.out.println(String.join(" -> ", lineage));
            }

        } else {
            System.out.println(sqlparser.getErrormessage());
        }


    }


}

