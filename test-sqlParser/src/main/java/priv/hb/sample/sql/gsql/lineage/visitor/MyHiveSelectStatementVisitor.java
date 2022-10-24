package priv.hb.sample.sql.gsql.lineage.visitor;

import java.util.ArrayList;
import java.util.List;

import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TExpressionList;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import io.vavr.Tuple2;
import lombok.Getter;
import priv.hb.sample.sql.gsql.utils.Common;

import static io.vavr.API.Tuple;

/**
 * @author hubin
 * @date 2022年10月10日 15:12
 */
public class MyHiveSelectStatementVisitor extends TParseTreeVisitor {

    // SQL中有哪些字段
    @Getter
    List<String> columns = new ArrayList<>();

    // SQL中有哪些字段,以及字段的来源表
    @Getter
    List<Tuple2<String, String>> columnAndSourceTables = new ArrayList<>();

    @Override
    public void preVisit(TExpression node) {
        super.preVisit(node);

        if (node.getLeftOperand() != null) {
            node.getLeftOperand().accept(this);
        }

        if (node.getRightOperand() != null) {
            node.getRightOperand().accept(this);
        }

        /*
         * 遍历子查询情况1  in和EXISTS的子查询
         *
         * select id as aid, name, address from table1 where pid>1 and pname in (select pname2 from table2)
         */
        if (node.getSubQuery() != null) {
            node.getSubQuery().accept(this);
        }

        if (node.getObjectOperand() != null) {
            String columnName = node.getObjectOperand().getPlainText();
            columns.add(columnName);
            TTable sourceTable = node.getObjectOperand().getSourceTable();

            if (sourceTable == null) {
                return;
            }

            if (sourceTable.getSubquery() != null) {
                String string = Common.getLineage(sourceTable.getSubquery().getPlainText()).get(0)._1().toString();
                columnAndSourceTables.add(Tuple(columnName, string));
            } else {
                columnAndSourceTables.add(Tuple(columnName, sourceTable.getPlainText()));

            }

        }


        if (node.getFunctionCall() != null) {
            TExpressionList args = node.getFunctionCall().getArgs();
            io.vavr.collection.List.ofAll(args).forEach(x -> x.accept(this));
        }


    }


    @Override
    public void preVisit(TJoin node) {
        super.preVisit(node);
        if (node.getTable() != null) {
            node.getTable().accept(this);
        }
    }

    @Override
    public void preVisit(TTable node) {
        super.preVisit(node);
        if (node.getSubquery() != null) {
            node.getSubquery().accept(this);
        }
    }


    @Override
    public void preVisit(TCTE node) {
        super.preVisit(node);

        if (node.getSubquery() != null) {
            node.getSubquery().accept(this);
        }

    }

    @Override
    public void preVisit(TSelectSqlStatement stmt) {
        super.preVisit(stmt);

        if (stmt.getCteList() != null) {
            io.vavr.collection.List.ofAll(stmt.getCteList()).forEach(x -> x.accept(this));
        }

        TSelectSqlStatement leftStmt = stmt.getLeftStmt();
        if (leftStmt != null) {
            stmt.getLeftStmt().accept(this);
        }

        TSelectSqlStatement rightStmt = stmt.getRightStmt();
        if (rightStmt != null) {
            stmt.getRightStmt().accept(this);
        }


        //不加if union all 会重复计算字段
        if (leftStmt == null && rightStmt == null) {
            TResultColumnList resultColumnList = stmt.getResultColumnList();
            for (TResultColumn tResultColumn : resultColumnList) {
                tResultColumn.getExpr().accept(this);
            }
        }

        if (stmt.getTables() != null) {
            io.vavr.collection.List.ofAll(stmt.getTables()).forEach(x -> x.accept(this));
        }

        if (stmt.getWhereClause() != null) {
            stmt.getWhereClause().getCondition().accept(this);
        }


        // if (stmt.getJoins() != null) {
        //     TJoinList joins = stmt.getJoins();
        //     io.vavr.collection.List.ofAll(joins).forEach(x -> x.accept(this));
        // }


    }


    @Override
    public void preVisit(TInsertSqlStatement stmt) {
        super.preVisit(stmt);

        if (stmt.getCteList() != null) {
            io.vavr.collection.List.ofAll(stmt.getCteList()).forEach(x -> x.accept(this));
        }

        if (stmt.getSubQuery() != null) {
            stmt.getSubQuery().accept(this);
        }

    }
}
