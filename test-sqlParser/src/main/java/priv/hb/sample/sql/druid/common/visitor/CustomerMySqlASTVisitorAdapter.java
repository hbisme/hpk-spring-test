package priv.hb.sample.sql.druid.common.visitor;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取表别名和字段别名的访问者
 * @author hubin
 * @date 2022年09月21日 11:02
 */
public class CustomerMySqlASTVisitorAdapter extends MySqlASTVisitorAdapter {
    private final Map<String, SQLTableSource> ALIAS_MAP = new HashMap<String, SQLTableSource>();
    private final Map<String, SQLExpr> ALIAS_COLUMN_MAP = new HashMap<String, SQLExpr>();

    @Override
    public boolean visit(SQLExprTableSource x) {
        String alias = x.getAlias();
        ALIAS_MAP.put(alias, x);
        return true;
    }


    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        List<SQLSelectItem> selectList = x.getSelectList();
        for (SQLSelectItem sqlSelectItem : selectList) {
            String alias = sqlSelectItem.getAlias();
            SQLExpr expr = sqlSelectItem.getExpr();
            ALIAS_COLUMN_MAP.put(alias, expr);
        }
        return true;
    }


    public Map<String, SQLTableSource> getAliasMap() {
        return ALIAS_MAP;
    }

    public Map<String, SQLExpr> getAliasColumnMap() {
        return ALIAS_COLUMN_MAP;

    }






}
