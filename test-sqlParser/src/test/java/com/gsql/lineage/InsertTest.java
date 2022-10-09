package com.gsql.lineage;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import io.vavr.API;
import io.vavr.Tuple2;

import static com.gsql.lineage.Common.getLineage;
import static io.vavr.API.Tuple;

/**
 * @author hubin
 * @date 2022年09月30日 16:59
 */
public class InsertTest {
    @Test
    public void test1() {
        //@formatter:off
        String sql = "insert overwrite directory '/tmp/tmp_sync_t_smc_coupon_owner_get_serial_b_id_temp' select null as id ,coupon_owner_get_serial_b_id from ytdw_temp.tmp_sync_t_smc_coupon_owner_get_serial_b_id ";
        //@formatter:on

        List<Tuple2<Set<String>, String>> lineage = getLineage(sql);
        Assert.assertEquals(lineage.size(), 1);
        Assert.assertEquals(lineage.get(0), Tuple(API.Set("ytdw_temp.tmp_sync_t_smc_coupon_owner_get_serial_b_id").toJavaSet(), ""));

        System.out.println(lineage);
    }

    @Test
    public void test2() {
        // @formatter:off
        String sql =
                "INSERT OVERWRITE TABLE table1.st_contract_gmv_d PARTITION (dayid='${v_date}')\n" +
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

        List<Tuple2<Set<String>, String>> lineage = getLineage(sql);
        Assert.assertEquals(lineage.size(), 1);
        Assert.assertEquals(lineage.get(0), Tuple(API.Set("ytdw.dwd_crm_contract_d").toJavaSet(), "table1.st_contract_gmv_d"));

        System.out.println(lineage);
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

        List<Tuple2<Set<String>, String>> lineage = getLineage(sql);
        Assert.assertEquals(lineage.size(), 1);
        Assert.assertEquals(lineage.get(0), Tuple(API.Set("ytdw.dim_hpc_shp_shop_d", "ytdw.ads_hpc_shp_shop_consumer_behavior_tag_d", "shop_brand_preference", "ytdw.ads_hpc_shp_shop_flow_behavior_tag_d", "shop_price_sensitivity", "shop_platform_active", "reco.st_shop_portrait_cate_preference_d").toJavaSet(), "ytdw.ads_hpc_shp_shop_preference_tag_d"));

        System.out.println(lineage);
    }
}
