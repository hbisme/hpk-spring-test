package com.druid.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.google.common.base.Splitter;
import com.druid.common.SqlAnalyze;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static com.druid.common.Utils.cast;


/**
 * @author hubin
 * @date 2022年09月20日 11:21
 */
// @formatter:off
public class TestWhereCondition {
    @Test
    public void test1() {
        String sql =
                "select\n" +
                "  '' as id,\n" +
                "  order_price_amt,\n" +
                "  order_pay_time,\n" +
                "  refund_create_time,\n" +
                "  shop_id,\n" +
                "  shop_pro_name,\n" +
                "  shop_pro_id,\n" +
                "  item_count,\n" +
                "  item_logistic_amount,\n" +
                "  item_id,\n" +
                "  item_total_amt,\n" +
                "  item_profit,\n" +
                "  item_purchase_profit,\n" +
                "  pay_type_amount,\n" +
                "  brand_id,\n" +
                "  brand_name,\n" +
                "  item_type,\n" +
                "  item_type_name,\n" +
                "  order_id,\n" +
                "  refund_id,\n" +
                "  item_amount,\n" +
                "  item_pay_amt,\n" +
                "  refund_real_amount,\n" +
                "  seller_id,\n" +
                "  category_id_first,\n" +
                "  category_id_first_name,\n" +
                "  item_name,\n" +
                "  yt_item_no,\n" +
                "  open_id,\n" +
                "  order_source\n" +
                "from\n" +
                "  ytdw.st_report_operation_d\n" +
                "where\n" +
                "  dayid = '20220814'\n" +
                "  and category_id_first not in ('11551', '381', '641', '12186')";

        SQLSelectStatement sqlSelectStatement = SqlAnalyze.parseSql(sql);
        List<String> targetColumnSet = Splitter.on(",").splitToStream("dayid,category_id_first").collect(Collectors.toList());
        boolean exactlyContains = SqlAnalyze.hasExactlyColumnsInWhereCondition(sqlSelectStatement, targetColumnSet);
        System.out.println(exactlyContains);

    }

    @Test
    public void test2() {
        String sql =
                "SELECT " +
                        "shop_id, " +
                        "length(nvl(cart_items, '')) as cnt " +
                  "FROM ytdw.ads_crm_shop_recommend_h ";

        SQLSelectStatement sqlSelectStatement = SqlAnalyze.parseSql(sql);
        boolean exactlyContains = SqlAnalyze.hasMultiWhereCondition(sqlSelectStatement);
        System.out.println(exactlyContains);
    }

    @Test
    public void modifySql1() {
        String sql =
                "SELECT " +
                        "shop_id, " +
                        "length(nvl(cart_items, '')) as cnt " +
                        "FROM ytdw.ads_crm_shop_recommend_h " +
                        "where id = 1";

        SQLSelectStatement sqlSelectStatement = SqlAnalyze.parseSql(sql);
        SQLSelectQuery query = sqlSelectStatement.getSelect().getQuery();
        SQLSelectQueryBlock queryBlock = cast(query, SQLSelectQueryBlock.class);
        SQLExprTableSource tableSource = cast(queryBlock.getFrom(), SQLExprTableSource.class);
        tableSource.setExpr(SQLUtils.toSQLExpr("crm.ads_crm_shop_recommend_h"));


        SQLBinaryOpExpr where = cast(queryBlock.getWhere(), SQLBinaryOpExpr.class);
        where.setRight(SQLUtils.toSQLExpr("2"));



        String res = SQLUtils.toSQLString(sqlSelectStatement);
        System.out.println(res);
    }


    @Test
    public void test3() {
        String sql =
                "SELECT " +
                        "shop_id, " +
                        "length(nvl(cart_items, '')) as cnt " +
                        "FROM ytdw.ads_crm_shop_recommend_h " +
                        "WHERE dayid = '20220212'";

        SQLSelectStatement sqlSelectStatement = SqlAnalyze.parseSql(sql);
        boolean exactlyContains = SqlAnalyze.hasMultiWhereCondition(sqlSelectStatement);
        System.out.println(exactlyContains);
    }

    @Test
    public void test4() {
        String sql =
                        "SELECT\n" +
                        "  shop_id,\n" +
                        "  length(nvl(cart_items, '')) as cnt\n" +
                        "FROM\n" +
                        "  ytdw.ads_crm_shop_recommend_h\n" +
                        "WHERE\n" +
                        "  dayid = '20220829'\n" +
                        "  and cart_items = 1222\n" +
                        "  AND length(nvl(cart_items, '')) < 32768";
        SQLSelectStatement sqlSelectStatement = SqlAnalyze.parseSql(sql);
        boolean exactlyContains = SqlAnalyze.hasMultiWhereCondition(sqlSelectStatement);
        System.out.println(exactlyContains);


    }


}
