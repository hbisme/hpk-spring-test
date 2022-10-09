package com.druid.common.visitor;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hubin
 * @date 2022年09月21日 11:11
 */
public class CustomerHiveSqlASTVisitorAdapter extends HiveASTVisitorAdapter {
    private final Map<String, SQLTableSource> ALIAS_MAP = new HashMap<String, SQLTableSource>();
    private final Map<String, SQLExpr> ALIAS_COLUMN_MAP = new HashMap<String, SQLExpr>();

    @Override
    public boolean visit(SQLExprTableSource x) {
        String alias = x.getAlias();
        ALIAS_MAP.put(alias, x);
        return super.visit(x);
    }

    @Override
    public void endVisit(SQLSelectItem sqlSelectItem) {
        String alias = sqlSelectItem.getAlias();
        SQLExpr expr = sqlSelectItem.getExpr();
        ALIAS_COLUMN_MAP.put(alias, expr);
    }



    @Override
    public boolean visit(SQLLimit x) {
        x.setRowCount(100);
        return super.visit(x);
    }

    public Map<String, SQLTableSource> getAliasMap() {
        return ALIAS_MAP;
    }

    public Map<String, SQLExpr> getAliasColumnMap() {
        return ALIAS_COLUMN_MAP;

    }


}
