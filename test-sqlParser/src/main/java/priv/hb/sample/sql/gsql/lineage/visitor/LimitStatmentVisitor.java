package priv.hb.sample.sql.gsql.lineage.visitor;

import gudusoft.gsqlparser.nodes.TLimitClause;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import lombok.Getter;

/**
 * @author hubin
 * @date 2022年10月09日 16:03
 */
public class LimitStatmentVisitor extends TParseTreeVisitor {
    @Getter
    private Long size;

    @Override
    public void preVisit(TLimitClause node) {
        super.preVisit(node);
        size = Long.valueOf(node.getOffset().toString());
    }

}
