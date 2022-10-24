package priv.hb.sample.sql.druid.common.visitor;

import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * 字段访问者
 * @author hubin
 * @date 2022年09月21日 11:11
 */
public class CustomerHiveSqlASTVisitorAdapter extends HiveASTVisitorAdapter {
    // SQL中有哪些字段
    @Getter
    List<String> columns = new ArrayList<>();



    @Override
    public boolean visit(SQLPropertyExpr x) {
        // 如果父节点是select,那他就是字段
        if (x.getParent().getClass() == SQLSelectItem.class) {
            columns.add(x.toString());
        }
        // where 语句中的 a = b
        if (x.getParent().getClass() == SQLBinaryOpExpr.class) {
            columns.add(x.toString());
        }

        // 函数调用的情况
        if (x.getParent().getClass() == SQLAggregateExpr.class) {
            columns.add(x.toString());
        }


        return super.visit(x);
    }


    @Override
    public boolean visit(SQLIdentifierExpr x) {
        // 如果父节点是select,那他就是字段
        if (x.getParent().getClass() == SQLSelectItem.class) {
            columns.add(x.toString());
        }

        // where 语句中的 a = b
        if (x.getParent().getClass() == SQLBinaryOpExpr.class) {
            columns.add(x.toString());
        }

        if (x.getParent().getClass() == SQLInSubQueryExpr.class) {
            columns.add(x.toString());
        }

        return super.visit(x);
    }

    @Override
    public boolean visit(SQLAllColumnExpr x) {
        columns.add(x.toString());
        return super.visit(x);
    }

    @Override
    public boolean visit(SQLSelectItem x) {


        return super.visit(x);
    }

    @Override
    public boolean visit(SQLExprTableSource x) {

        return super.visit(x);
    }


    @Override
    public boolean visit(SQLLimit x) {
        x.setRowCount(100);
        return super.visit(x);
    }


    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        List<SQLSelectItem> selectList = x.getSelectList();


        return super.visit(x);

    }

    @Override
    public boolean visit(SQLBinaryOpExpr x) {

        return super.visit(x);
    }



}
