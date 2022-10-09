package com.druid.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;

import org.junit.Test;

import java.util.List;

/**
 * @author hubin
 * @date 2022年09月20日 15:01
 */
public class TestInsert {
    @Test
    public void test1() {
        String sql = "insert overwrite directory '/tmp/tmp_sync_t_smc_coupon_owner_get_serial_b_id_temp' select null as id ,coupon_owner_get_serial_b_id from ytdw_temp.tmp_sync_t_smc_coupon_owner_get_serial_b_id ";

        String replace = sql.replace("directory", "table");
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(replace, DbType.hive);

        HiveInsertStatement hiveInsertStatement = (HiveInsertStatement) sqlStatements.get(0);
        SQLExprTableSource tableSource = hiveInsertStatement.getTableSource();

        SQLSelect query = hiveInsertStatement.getQuery();
        SQLSelectQueryBlock select = (SQLSelectQueryBlock) query.getQuery();

        List<SQLSelectItem> selectList = select.getSelectList();


        System.out.println(selectList);
    }
}
