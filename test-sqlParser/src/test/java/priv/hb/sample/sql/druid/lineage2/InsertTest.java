package priv.hb.sample.sql.druid.lineage2;

import com.alibaba.druid.sql.ast.SQLStatement;





import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;

import priv.hb.sample.sql.druid.common.visitor.HiveLineageVisitor;

import org.junit.jupiter.api.Test;



/**
 * @author hubin
 * @date 2022年09月20日 15:01
 */
public class InsertTest {
    @Test
    public void test1() {
        String sql = "insert overwrite directory '/tmp/tmp_sync_t_smc_coupon_owner_get_serial_b_id_temp' select null as id ,coupon_owner_get_serial_b_id from ytdw_temp.tmp_sync_t_smc_coupon_owner_get_serial_b_id ";

        String replace = sql.replace("directory", "table");

        HiveStatementParser parser = new HiveStatementParser(replace);
        SQLStatement sqlStatement = parser.parseStatement();

        HiveLineageVisitor hiveLineageVisitor = new HiveLineageVisitor();
        sqlStatement.accept(hiveLineageVisitor);

        System.out.println(hiveLineageVisitor.getSource());
        System.out.println(hiveLineageVisitor.getTarget());
    }


    @Test
    public void test2() {
        // @formatter:off
        String sql =
                "INSERT OVERWRITE TABLE table1.st_contract_gmv_d PARTITION (dayid='${v_date}', hour='11')\n" +
                        "SELECT   crm_contract_shop.contract_id,\n" +
                        "         crm_contract_shop.shop_id,\n" +
                        "         sum(if(shop_trd.date_id >= substr(crm_contract.start_time,\n" +
                        "         1,\n" +
                        "         8)\n" +
                        "        AND crm_contract.date_id <= substr(crm_contract.end_time, 1, 8), crm_contract.net_pay_amt_1d_a_style_frez, 0)) AS a_gmv, sum(if(shop_trd.date_id >= substr(crm_contract.start_time, 1, 8)\n" +
                        "        AND crm_contract.date_id <= substr(crm_contract.end_time, 1, 8), crm_contract.net_pay_amt_1d_b_style_frez, 0)) AS b_gmv\n" +
                        "FROM \n" +
                        "    (SELECT *\n" +
                        "    FROM ytdw.dwd_crm_contract_d\n" +
                        "    WHERE dayid = '${v_date}'\n" +
                        "    AND is_deleted = 0 ) crm_contract";
        // @formatter:on

        HiveStatementParser parser = new HiveStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();

        HiveLineageVisitor hiveLineageVisitor = new HiveLineageVisitor();
        sqlStatement.accept(hiveLineageVisitor);

        System.out.println(hiveLineageVisitor.getSource());
        System.out.println(hiveLineageVisitor.getTarget());
    }

    @Test
    public void test3() {
        // @formatter:off
        String sql =
                "insert overwrite table ytdw.ads_hpc_shp_shop_preference_tag_d partition (dayid = '20220820')\n" +
                        "SELECT\n" +
                        "  hpc_shop.shop_id,\n" +
                        "  hpc_shop.shop_name,\n" +
                        "  shop_category_preference.category_preference,\n" +
                        "  shop_brand_preference.brand_preference,\n" +
                        "  shop_price_sensitivity.price_sensitivity,\n" +
                        "  shop_platform_active.shop_platform_active_preference,\n" +
                        "  shop_consumer_behavior.shop_item_milk_type_preference,\n" +
                        "  shop_consumer_behavior.shop_item_milk_type_name_preference,\n" +
                        "  shop_consumer_behavior.shop_jk_milk_brand_preference_only_for_op,\n" +
                        "  shop_consumer_behavior.shop_gc_milk_brand_preference_only_for_op,\n" +
                        "  shop_consumer_behavior.shop_kj_milk_brand_preference_only_for_op,\n" +
                        "  shop_consumer_behavior.shop_diapers_brand_preference_only_for_op,\n" +
                        "  CASE\n" +
                        "    WHEN shop_flow_behavior.area_expo_pv_3m_item_detail_seckill_coupon >= 1 THEN 1\n" +
                        "    ELSE 0\n" +
                        "  END AS is_seckill_active_preference_shop,\n" +
                        "  shop_consumer_behavior.shop_none_milk_diapers_brand_id_preference,\n" +
                        "  shop_consumer_behavior.shop_none_milk_diapers_brand_name_preference\n" +
                        "FROM\n" +
                        "  (\n" +
                        "    SELECT\n" +
                        "      shop_id,\n" +
                        "      shop_name\n" +
                        "    FROM\n" +
                        "      ytdw.dim_hpc_shp_shop_d\n" +
                        "    WHERE\n" +
                        "      dayid = '20220820'\n" +
                        "      AND shop_status != 6\n" +
                        "      AND shop_is_inuse = 1\n" +
                        "  ) AS hpc_shop\n" +
                        "  LEFT JOIN (\n" +
                        "    SELECT\n" +
                        "      shop_id,\n" +
                        "      category_preference\n" +
                        "    FROM\n" +
                        "      reco.st_shop_portrait_cate_preference_d\n" +
                        "    WHERE\n" +
                        "      dayid = '20220820'\n" +
                        "  ) AS shop_category_preference ON shop_category_preference.shop_id = hpc_shop.shop_id\n" +
                        "  LEFT JOIN (\n" +
                        "    SELECT\n" +
                        "      shop_id,\n" +
                        "      pay_itm_milk_type_list_3m AS shop_item_milk_type_preference,\n" +
                        "      pay_itm_milk_type_name_list_3m AS shop_item_milk_type_name_preference,\n" +
                        "      shop_jk_milk_brand_name_preference AS shop_jk_milk_brand_preference_only_for_op,\n" +
                        "      shop_gc_milk_brand_name_preference AS shop_gc_milk_brand_preference_only_for_op,\n" +
                        "      shop_kj_milk_brand_name_preference AS shop_kj_milk_brand_preference_only_for_op,\n" +
                        "      shop_diapers_brand_name_preference AS shop_diapers_brand_preference_only_for_op,\n" +
                        "      shop_none_milk_diapers_brand_id_preference,\n" +
                        "      shop_none_milk_diapers_brand_name_preference\n" +
                        "    FROM\n" +
                        "      ytdw.ads_hpc_shp_shop_consumer_behavior_tag_d\n" +
                        "    WHERE\n" +
                        "      dayid = '20220820'\n" +
                        "  ) AS shop_consumer_behavior ON shop_consumer_behavior.shop_id = hpc_shop.shop_id\n" +
                        "  LEFT JOIN (\n" +
                        "    SELECT\n" +
                        "      shop_id,\n" +
                        "      area_expo_pv_3m_item_detail_seckill_coupon\n" +
                        "    FROM\n" +
                        "      ytdw.ads_hpc_shp_shop_flow_behavior_tag_d\n" +
                        "    WHERE\n" +
                        "      dayid = '20220820'\n" +
                        "  ) AS shop_flow_behavior ON shop_flow_behavior.shop_id = hpc_shop.shop_id\n" +
                        "  LEFT JOIN shop_brand_preference ON shop_brand_preference.shop_id = hpc_shop.shop_id\n" +
                        "  LEFT JOIN shop_price_sensitivity ON shop_price_sensitivity.shop_id = hpc_shop.shop_id\n" +
                        "  LEFT JOIN shop_platform_active ON shop_platform_active.shop_id = hpc_shop.shop_id";
        // @formatter:on

        HiveStatementParser parser = new HiveStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();

        HiveLineageVisitor hiveLineageVisitor = new HiveLineageVisitor();
        sqlStatement.accept(hiveLineageVisitor);

        System.out.println(hiveLineageVisitor.getSource());
        System.out.println(hiveLineageVisitor.getTarget());
        System.out.println(hiveLineageVisitor.getTemporaries());


    }


    /**
     * insert访问者获取结果表的分区信息(有分区),和结果表名
     */
    @org.junit.jupiter.api.Test
    public void test4() {
        // @formatter:off
        String sql =
                "INSERT OVERWRITE TABLE table1.st_contract_gmv_d PARTITION (dayid='${v_date}', hour='11')\n" +
                        "SELECT   crm_contract.contract_id,\n" +
                        "         crm_contract.shop_id,\n" +
                        "         sum(if(shop_trd.date_id >= substr(crm_contract.start_time,\n" +
                        "         1,\n" +
                        "         8)\n" +
                        "        AND shop_trd.date_id <= substr(crm_contract.end_time, 1, 8), shop_trd.net_pay_amt_1d_a_style_frez, 0)) AS a_gmv, sum(if(shop_trd.date_id >= substr(crm_contract.start_time, 1, 8)\n" +
                        "        AND shop_trd.date_id <= substr(crm_contract.end_time, 1, 8), shop_trd.net_pay_amt_1d_b_style_frez, 0)) AS b_gmv\n" +
                        "FROM \n" +
                        "    (SELECT *\n" +
                        "    FROM ytdw.dwd_crm_contract_d\n" +
                        "    WHERE dayid = '${v_date}'\n" +
                        "    AND is_deleted = 0 ) crm_contract";
        // @formatter:on

        HiveStatementParser parser = new HiveStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();

        HiveLineageVisitor hiveLineageVisitor = new HiveLineageVisitor();
        sqlStatement.accept(hiveLineageVisitor);

        System.out.println(hiveLineageVisitor.getSource());
        System.out.println(hiveLineageVisitor.getTarget());
        System.out.println(hiveLineageVisitor.getTemporaries());
    }


    /**
     * insert访问者获取结果表的分区信息(无分区) 和结果表名
     */
    @org.junit.jupiter.api.Test
    public void test5() {
        // @formatter:off
        String sql =
                "INSERT OVERWRITE TABLE table1.st_contract_gmv_d \n" +
                        "SELECT   crm_contract_shop.contract_id,\n" +
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
                        "    AND is_deleted = 0 ) crm_contract";
        // @formatter:on

        HiveStatementParser parser = new HiveStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();

        HiveLineageVisitor hiveLineageVisitor = new HiveLineageVisitor();
        sqlStatement.accept(hiveLineageVisitor);

        System.out.println(hiveLineageVisitor.getSource());
        System.out.println(hiveLineageVisitor.getTarget());
        System.out.println(hiveLineageVisitor.getTemporaries());

    }

    // insert overwrite table test.test2 partition(dt, hour) select `(dt|hour)?+.+`,dt,hour from test.test;


    /**
     * insert访问者获取结果表的动态分区信息
     *
     */
    @org.junit.jupiter.api.Test
    public void test6() {
        // @formatter:off
        String sql =
                "insert overwrite table test.test2 partition(dt, hour) select `c1`,dt,hour from test.test;";
        // @formatter:on

        HiveStatementParser parser = new HiveStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();

        HiveLineageVisitor hiveLineageVisitor = new HiveLineageVisitor();
        sqlStatement.accept(hiveLineageVisitor);

        System.out.println(hiveLineageVisitor.getSource());
        System.out.println(hiveLineageVisitor.getTarget());
        System.out.println(hiveLineageVisitor.getTemporaries());
    }
}
