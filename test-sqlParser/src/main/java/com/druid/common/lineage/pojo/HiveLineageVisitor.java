package com.druid.common.lineage.pojo;

import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import io.vavr.collection.List;
import lombok.Getter;

import static com.druid.common.Utils.cast;

/**
 * @author hubin
 * @date 2022年09月21日 16:58
 */
public class HiveLineageVisitor extends HiveASTVisitorAdapter {

    @Getter
    private final Set<Source> source = new HashSet<>();

    @Getter
    private Target target = null;

    @Getter
    private final Set<Temporary> temporaries = new HashSet<>();


    @Override
    public void endVisit(SQLExprTableSource x) {
        // 去掉insert Into 到的记过表,其是结果表不是源表
        if (x.getParent().getClass() != HiveInsertStatement.class) {
            Source source = Source.of(x.getSchema(), x.getTableName());
            this.source.add(source);
        }

        super.endVisit(x);
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        System.out.println(x.getTableName() + "=====");
        return super.visit(x);
    }

    @Override
    public boolean visit(HiveInsertStatement x) {
        SQLExprTableSource tableSource = x.getTableSource();
        String schema = tableSource.getSchema();
        String tableName = tableSource.getTableName();
        this.target = Target.of(schema, tableName);

        SQLWithSubqueryClause with = x.getWith();
        if (with != null) {
            for (SQLWithSubqueryClause.Entry entry : with.getEntries()) {
                String alias = entry.getAlias();
                Temporary temporary = new Temporary(alias);
                temporaries.add(temporary);
                // 框架没有访问with里面的sql语句
                this.visit(entry.getSubQuery());
            }
        }

        return super.visit(x);
    }

    @Override
    public boolean visit(SQLSelect x) {
        SQLSelectQuery query = x.getQuery();
        if(query instanceof  SQLSelectQueryBlock) {
            SQLSelectQueryBlock cast = cast(query, SQLSelectQueryBlock.class);
            SQLTableSource from = cast.getFrom();


        }


        return super.visit(x);
    }

    public Set<Source> getSource() {
        java.util.List<String> tempTables = List.ofAll(temporaries).map(x -> x.getTableName()).toJavaList();
        Set<Source> sources = List.ofAll(source).filter(x -> !(x.getDbName() == null && tempTables.contains(x.getTableName()))).toJavaSet();
        return sources;

    }


    /**
     * 不知道为什么访问不到
     */
    @Override
    public void endVisit(SQLWithSubqueryClause x) {
        super.endVisit(x);
    }

    /**
     * 不知道为什么访问不到
     */
    @Override
    public boolean visit(SQLWithSubqueryClause x) {
        return super.visit(x);
    }


}
