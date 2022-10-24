package priv.hb.sample.sql.gsql.lineage.visitor;

import java.util.Set;

import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import io.vavr.Function0;
import lombok.Getter;
import priv.hb.sample.sql.gsql.utils.Common;

/**
 * @author hubin
 * @date 2022年10月09日 18:13
 */
public class TSelectSqlStatementVisitor extends TParseTreeVisitor {
    @Getter
    private Long limitSize = null;

    @Getter
    private Set<String> sourceTableNames = io.vavr.collection.List.<String>empty().toJavaSet();

    @Getter
    private String targetTableName;

    @Override
    public void preVisit(TSelectSqlStatement stmt) {
        super.preVisit(stmt);

        Function0<Long> limitFunc = () -> {
            if (stmt.getLimitClause() != null) {
                LimitStatmentVisitor limitStatmentVisitor = new LimitStatmentVisitor();
                stmt.getLimitClause().accept(limitStatmentVisitor);
                return limitStatmentVisitor.getSize();
            } else {
                return null;
            }
        };

        limitSize = limitFunc.get();

        sourceTableNames = Common.getSourceTableNamesFunc(stmt, io.vavr.collection.List.<String>empty().toJavaSet());

        targetTableName = Common.getTargetTableNameFunc(stmt);
    }


}
