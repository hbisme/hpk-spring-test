package priv.hb.sample.sql.druid.common.visitor;

import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitorAdapter;

import priv.hb.sample.sql.druid.common.pojo.Source;
import priv.hb.sample.sql.druid.common.pojo.Target;
import priv.hb.sample.sql.druid.common.pojo.Temporary;

import java.util.HashSet;
import java.util.Set;

import io.vavr.collection.List;
import lombok.Getter;

/**
 * hive表血缘访问者
 *
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
    public boolean visit(SQLExprTableSource x) {
        if (x.getParent().getClass() == SQLSelectQueryBlock.class) {
            Source source = Source.of(x.getSchema(), x.getTableName());
            this.source.add(source);
        }

        return super.visit(x);
    }

    @Override
    public boolean visit(SQLCreateViewStatement x) {
        SQLExprTableSource tableSource = x.getTableSource();
        Temporary temporary = new Temporary(tableSource.getExpr().toString());
        temporaries.add(temporary);

        return super.visit(x);
    }

    @Override
    public boolean visit(HiveCreateTableStatement x) {
        SQLExprTableSource tableSource = x.getTableSource();
        String schema = tableSource.getSchema();
        String tableName = tableSource.getTableName();
        this.target = Target.of(schema, tableName);
        return super.visit(x);
    }

    @Override
    public boolean visit(HiveInsertStatement x) {
        SQLExprTableSource tableSource = x.getTableSource();
        String schema = tableSource.getSchema();
        String tableName = tableSource.getTableName();
        this.target = Target.of(schema, tableName);

        java.util.List<SQLAssignItem> partitions = x.getPartitions();
        List.ofAll(partitions).forEach(p -> {
            String key = p.getTarget().toString();

            if (p.getValue() != null) {
                String value = p.getValue().toString();
                target.addPartitions(key, value);
            } else {
                target.addPartitions(key, null);

            }

        });

        SQLWithSubqueryClause with = x.getWith();
        if (with != null) {
            for (SQLWithSubqueryClause.Entry entry : with.getEntries()) {
                String alias = entry.getAlias();
                Temporary temporary = new Temporary(alias);
                temporaries.add(temporary);
                // 框架没有访问with里面的sql语句
                this.visit(entry.getSubQuery());

                if (entry.getSubQuery() != null) {
                    entry.getSubQuery().accept(this);
                }
            }
        }


        return super.visit(x);
    }


    public Set<Source> getSource() {
        java.util.List<String> tempTables = List.ofAll(temporaries).map(x -> x.getTableName()).toJavaList();
        Set<Source> sources = List.ofAll(source).filter(x -> !(x.getDbName() == null && tempTables.contains(x.getTableName()))).toJavaSet();
        return sources;
    }


}
