package priv.hb.sample.sql.druid.visit;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveSchemaStatVisitor;

import org.junit.jupiter.api.Test;

/**
 * druid自带的 MySqlSchemaStatVisitor 测试
 * @author hubin
 * @date 2022年10月12日 17:11
 */
public class DruidVisitTest {

    @Test
    public void test1() {
        // @formatter:off
        String sql =
                "INSERT OVERWRITE TABLE table1.st_contract_gmv_d PARTITION (dayid='${v_date}')\n" +
                        "SELECT crm_contract_shop.contract_id,\n" +
                        "         crm_contract_shop.shop_id,\n" +
                        "         sum(if(shop_trd.date_id >= substr(crm_contract.start_time,\n" +
                        "         1,\n" +
                        "         8)\n" +
                        "        AND shop_trd.date_id <= substr(crm_contract.end_time, 1, 8), shop_trd.net_pay_amt_1d_a_style_frez, 0)) AS a_gmv, sum(if(shop_trd.date_id >= substr(crm_contract.start_time, 1, 8)\n" +
                        "        AND shop_trd.date_id <= substr(crm_contract.end_time, 1, 8), shop_trd.net_pay_amt_1d_b_style_frez, 0)) AS b_gmv\n" +
                        "FROM \n" +
                        "    (SELECT *\n" +
                        "    FROM ytdw.dwd_crm_contract_d\n" +
                        "    WHERE dayid = '${v_date}'\n" +
                        "    AND is_deleted = 0 ) crm_contract \n" +
                        "WHERE contract_id>10";
        // @formatter:on


        // 解析SQL
        HiveStatementParser parser = new HiveStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();



        HiveSchemaStatVisitor mysqlVisitor = new HiveSchemaStatVisitor();
        sqlStatement.accept(mysqlVisitor);

        System.out.println("使用visitor数据表：" + mysqlVisitor.getTables());
        System.out.println("使用visitor字段：" + mysqlVisitor.getColumns());
        System.out.println("使用visitor条件：" + mysqlVisitor.getConditions());
        System.out.println("使用visitor分组：" + mysqlVisitor.getGroupByColumns());
        System.out.println("使用visitor排序：" + mysqlVisitor.getOrderByColumns());


    }
}
