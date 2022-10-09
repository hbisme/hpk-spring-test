package com.gsql.lineage;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import io.vavr.API;
import io.vavr.Tuple2;

import static com.gsql.lineage.Common.getLineage;
import static io.vavr.API.Tuple;

/**
 * @author hubin
 * @date 2022年09月30日 17:17
 */
public class CTETest {
    @Test
    public void test1() {
        //@formatter:off
        String sql = "with tmp1 as ( select key from srcTable where key = '5')\n" +
                        "select *\n" +
                        "from tmp1;\n";
        //@formatter:on

        List<Tuple2<Set<String>, String>> lineage = getLineage(sql);
        Assert.assertEquals(lineage.size(), 1);
        Assert.assertEquals(lineage.get(0), Tuple(API.Set("srcTable").toJavaSet(), ""));

        System.out.println(lineage);

    }


    @Test
    public void test2() {
        //@formatter:off
        String sql =
                "with hive_info as (\n" +
                        "  select\n" +
                        "    max(dayid) as cur_dayid,\n" +
                        "    round(\n" +
                        "      sum(\n" +
                        "        case\n" +
                        "          when dayid <> '20220820' then a.tbl_size_G\n" +
                        "          else 0\n" +
                        "        end\n" +
                        "      ) / 1024,\n" +
                        "      2\n" +
                        "    ) as hive_size_pre,\n" +
                        "    round(\n" +
                        "      sum(\n" +
                        "        case\n" +
                        "          when dayid = '20220820' then a.tbl_size_G\n" +
                        "          else 0\n" +
                        "        end\n" +
                        "      ) / 1024,\n" +
                        "      2\n" +
                        "    ) as hive_size_cur,\n" +
                        "    round(\n" +
                        "      sum(\n" +
                        "        case\n" +
                        "          when dayid = '20220820'\n" +
                        "          and b.tbl_name is not null then a.tbl_size_G\n" +
                        "          else 0\n" +
                        "        end\n" +
                        "      ) / 1024,\n" +
                        "      2\n" +
                        "    ) as hive_inused_tb_size_cur,\n" +
                        "    round(\n" +
                        "      sum(\n" +
                        "        case\n" +
                        "          when dayid = '20220820'\n" +
                        "          and max_dayid >= '20220721' then a.tbl_size_G\n" +
                        "          else 0\n" +
                        "        end\n" +
                        "      ) / 1024,\n" +
                        "      2\n" +
                        "    ) as hive_inused_tb_size_last_30d,\n" +
                        "    round(\n" +
                        "      sum(\n" +
                        "        case\n" +
                        "          when dayid = '20220820'\n" +
                        "          and max_dayid >= '20220522' then a.tbl_size_G\n" +
                        "          else 0\n" +
                        "        end\n" +
                        "      ) / 1024,\n" +
                        "      2\n" +
                        "    ) as hive_inused_tb_size_last_90d,\n" +
                        "    round(\n" +
                        "      sum(\n" +
                        "        case\n" +
                        "          when dayid = '20220820'\n" +
                        "          and b.tbl_name is null then a.tbl_size_G\n" +
                        "          else 0\n" +
                        "        end\n" +
                        "      ) / 1024,\n" +
                        "      2\n" +
                        "    ) as hive_unused_tb_size_cur,\n" +
                        "    round(\n" +
                        "      sum(\n" +
                        "        case\n" +
                        "          when dayid <> '20220820'\n" +
                        "          or (\n" +
                        "            dayid = '20220820'\n" +
                        "            and max_dayid >= '20220721'\n" +
                        "          ) then 0\n" +
                        "          else a.tbl_size_G\n" +
                        "        end\n" +
                        "      ) / 1024,\n" +
                        "      2\n" +
                        "    ) as hive_unused_tb_size_last_30d,\n" +
                        "    round(\n" +
                        "      sum(\n" +
                        "        case\n" +
                        "          when dayid <> '20220820'\n" +
                        "          or (\n" +
                        "            dayid = '20220820'\n" +
                        "            and max_dayid >= '20220522'\n" +
                        "          ) then 0\n" +
                        "          else a.tbl_size_G\n" +
                        "        end\n" +
                        "      ) / 1024,\n" +
                        "      2\n" +
                        "    ) as hive_unused_tb_size_last_90d,\n" +
                        "    sum(\n" +
                        "      case\n" +
                        "        when dayid = '20220820' then 1\n" +
                        "        else 0\n" +
                        "      end\n" +
                        "    ) as hive_tb_cnt_cur,\n" +
                        "    sum(\n" +
                        "      case\n" +
                        "        when dayid = '20220820'\n" +
                        "        and b.tbl_name is not null then 1\n" +
                        "        else 0\n" +
                        "      end\n" +
                        "    ) as hive_inused_tb_cnt_cur,\n" +
                        "    sum(\n" +
                        "      case\n" +
                        "        when dayid = '20220820'\n" +
                        "        and max_dayid >= '20220721' then 1\n" +
                        "        else 0\n" +
                        "      end\n" +
                        "    ) as hive_inused_tb_cnt_last_30d,\n" +
                        "    sum(\n" +
                        "      case\n" +
                        "        when dayid = '20220820'\n" +
                        "        and max_dayid >= '20220522' then 1\n" +
                        "        else 0\n" +
                        "      end\n" +
                        "    ) as hive_inused_tb_cnt_last_90d,\n" +
                        "    sum(\n" +
                        "      case\n" +
                        "        when dayid = '20220820'\n" +
                        "        and b.tbl_name is null then 1\n" +
                        "        else 0\n" +
                        "      end\n" +
                        "    ) as hive_unused_tb_cnt_cur,\n" +
                        "    sum(\n" +
                        "      case\n" +
                        "        when dayid <> '20220820'\n" +
                        "        or (\n" +
                        "          dayid = '20220820'\n" +
                        "          and max_dayid >= '20220721'\n" +
                        "        ) then 0\n" +
                        "        else 1\n" +
                        "      end\n" +
                        "    ) as hive_unused_tb_cnt_last_30d,\n" +
                        "    sum(\n" +
                        "      case\n" +
                        "        when dayid <> '20220820'\n" +
                        "        or (\n" +
                        "          dayid = '20220820'\n" +
                        "          and max_dayid >= '20220522'\n" +
                        "        ) then 0\n" +
                        "        else 1\n" +
                        "      end\n" +
                        "    ) as hive_unused_tb_cnt_last_90d\n" +
                        "  from\n" +
                        "    (\n" +
                        "      select\n" +
                        "        db_name,\n" +
                        "        tbl_name,\n" +
                        "        tbl_size_G,\n" +
                        "        dayid\n" +
                        "      from\n" +
                        "        dws_ytj_met_tb_pt_storage_reduce_detail_di\n" +
                        "      where\n" +
                        "        dayid > '20220818'\n" +
                        "    ) a\n" +
                        "    left join (\n" +
                        "      select\n" +
                        "        db_name,\n" +
                        "        tbl_name,\n" +
                        "        max(dayid) as dayid_max,\n" +
                        "        count(distinct dayid) as dayid_cnt,\n" +
                        "        max(dayid) as max_dayid\n" +
                        "      from\n" +
                        "        dim_ytj_met_tb_life_cycle_detail_di\n" +
                        "      where\n" +
                        "        dayid > '0'\n" +
                        "      group by\n" +
                        "        db_name,\n" +
                        "        tbl_name\n" +
                        "    ) b on a.tbl_name = b.tbl_name\n" +
                        "    and a.db_name = b.db_name\n" +
                        "),\n" +
                        "\n" +

                        "hdfs_info as (\n" +
                        "  select\n" +
                        "    round(\n" +
                        "      sum(\n" +
                        "        case\n" +
                        "          when dayid = '20220820' then replication * file_size\n" +
                        "          else 0\n" +
                        "        end\n" +
                        "      ) / 1024 / 1024 / 1024 / 1024,\n" +
                        "      2\n" +
                        "    ) as hdfs_size_cur,\n" +
                        "    round(\n" +
                        "      sum(\n" +
                        "        case\n" +
                        "          when dayid <> '20220820' then replication * file_size\n" +
                        "          else 0\n" +
                        "        end\n" +
                        "      ) / 1024 / 1024 / 1024 / 1024,\n" +
                        "      2\n" +
                        "    ) as hdfs_size_pre\n" +
                        "  from\n" +
                        "    dwd_hdfs_meta_d\n" +
                        "  where\n" +
                        "    dayid > '20220818'\n" +
                        "    and dayid <= '20220820'\n" +
                        ")\n" +
                        "\n" +

                        "insert into ads_met_hdfs_hive_size_d\n" +
                        "SELECT\n" +
                        "  cur_dayid,\n" +
                        "  hdfs_size_cur,\n" +
                        "  hdfs_size_pre,\n" +
                        "  hive_size_cur,\n" +
                        "  hive_size_pre,\n" +
                        "  hive_inused_tb_size_cur,\n" +
                        "  hive_inused_tb_size_last_30d,\n" +
                        "  hive_inused_tb_size_last_90d,\n" +
                        "  hive_unused_tb_size_cur,\n" +
                        "  hive_unused_tb_size_last_30d,\n" +
                        "  hive_unused_tb_size_last_90d,\n" +
                        "  hive_tb_cnt_cur,\n" +
                        "  hive_inused_tb_cnt_cur,\n" +
                        "  hive_inused_tb_cnt_last_30d,\n" +
                        "  hive_inused_tb_cnt_last_90d,\n" +
                        "  hive_unused_tb_cnt_cur,\n" +
                        "  hive_unused_tb_cnt_last_30d,\n" +
                        "  hive_unused_tb_cnt_last_90d,\n" +
                        "  from_unixtime(unix_timestamp(), 'yyyyMMddHHmmss') as insert_time\n" +
                        "from\n" +
                        "  ytdw.src_hive_info\n" +
                        "  cross join hdfs_info";
        //@formatter:on

        List<Tuple2<Set<String>, String>> lineage = getLineage(sql);
        Assert.assertEquals(lineage.size(), 1);
        Assert.assertEquals(lineage.get(0), Tuple(API.Set("dwd_hdfs_meta_d","dws_ytj_met_tb_pt_storage_reduce_detail_di", "ytdw.src_hive_info", "dim_ytj_met_tb_life_cycle_detail_di").toJavaSet(), "ads_met_hdfs_hive_size_d"));

        System.out.println(lineage);

    }


    @Test
    public void test3(){
        //@formatter:off
        String sql =
                "with gmv_tmp AS (\n" +
                        "  SELECT\n" +
                        "    shop_id,\n" +
                        "    brand_id,\n" +
                        "    sum(\n" +
                        "      if(\n" +
                        "        date_id >= '20220514'\n" +
                        "        AND date_id <= '20220814'\n" +
                        "        AND category_1st_id = 10,\n" +
                        "        pay_amt_1d,\n" +
                        "        0\n" +
                        "      )\n" +
                        "    ) AS month3_cate10_gmv,\n" +
                        "    sum(\n" +
                        "      if(\n" +
                        "        date_id >= '20220215'\n" +
                        "        AND date_id <= '20220814'\n" +
                        "        AND category_1st_id = 10,\n" +
                        "        pay_amt_1d,\n" +
                        "        0\n" +
                        "      )\n" +
                        "    ) AS month6_cate10_gmv,\n" +
                        "    sum(\n" +
                        "      if(\n" +
                        "        date_id >= '20220514'\n" +
                        "        AND date_id <= '20220814'\n" +
                        "        AND category_1st_id != 10,\n" +
                        "        pay_amt_1d,\n" +
                        "        0\n" +
                        "      )\n" +
                        "    ) AS month3_cate_other_gmv,\n" +
                        "    sum(\n" +
                        "      if(\n" +
                        "        date_id >= '20220215'\n" +
                        "        AND date_id <= '20220814'\n" +
                        "        AND category_1st_id != 10,\n" +
                        "        pay_amt_1d,\n" +
                        "        0\n" +
                        "      )\n" +
                        "    ) AS month6_cate_other_gmv\n" +
                        "  FROM\n" +
                        "    dws_hpc_trd_shop_item_d\n" +
                        "  WHERE\n" +
                        "    dayid = '20220814'\n" +
                        "    AND is_pay_bp = 1\n" +
                        "    AND category_1st_id != 610\n" +
                        "    AND date_id >= '20210212'\n" +
                        "    AND date_id <= '20220814'\n" +
                        "  GROUP BY\n" +
                        "    shop_id,\n" +
                        "    brand_id\n" +
                        "),\n" +
                        "\n" +

                        "refund_tmp AS (\n" +
                        "  SELECT\n" +
                        "    t1.shop_id,\n" +
                        "    t2.brand_id,\n" +
                        "    sum(\n" +
                        "      if(\n" +
                        "        substr(t1.refund_end_time, 1, 8) >= '20220514'\n" +
                        "        AND substr(t1.refund_end_time, 1, 8) <= '20220814'\n" +
                        "        AND t1.refund_status = 9\n" +
                        "        AND t2.category_1st_id = 10,\n" +
                        "        t1.refund_actual_amount,\n" +
                        "        0\n" +
                        "      )\n" +
                        "    ) AS month3_cate10_refund_amt,\n" +
                        "    sum(\n" +
                        "      if(\n" +
                        "        substr(t1.refund_end_time, 1, 8) >= '20220215'\n" +
                        "        AND substr(t1.refund_end_time, 1, 8) <= '20220814'\n" +
                        "        AND t1.refund_status = 9\n" +
                        "        AND t2.category_1st_id = 10,\n" +
                        "        t1.refund_actual_amount,\n" +
                        "        0\n" +
                        "      )\n" +
                        "    ) AS month6_cate10_refund_amt,\n" +
                        "    sum(\n" +
                        "      if(\n" +
                        "        substr(t1.refund_end_time, 1, 8) >= '20220514'\n" +
                        "        AND substr(t1.refund_end_time, 1, 8) <= '20220814'\n" +
                        "        AND t1.refund_status = 9\n" +
                        "        AND t2.category_1st_id != 10,\n" +
                        "        t1.refund_actual_amount,\n" +
                        "        0\n" +
                        "      )\n" +
                        "    ) AS month3_cate_other_refund_amt,\n" +
                        "    sum(\n" +
                        "      if(\n" +
                        "        substr(t1.refund_end_time, 1, 8) >= '20220215'\n" +
                        "        AND substr(t1.refund_end_time, 1, 8) <= '20220814'\n" +
                        "        AND t1.refund_status = 9\n" +
                        "        AND t2.category_1st_id != 10,\n" +
                        "        t1.refund_actual_amount,\n" +
                        "        0\n" +
                        "      )\n" +
                        "    ) AS month6_cate_other_refund_amt\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        *\n" +
                        "      FROM\n" +
                        "        dw_afs_order_refund_new_d\n" +
                        "      WHERE\n" +
                        "        dayid = '20220814'\n" +
                        "        AND multiple_refund = 0\n" +
                        "        AND bu_id = 0\n" +
                        "        AND substr(refund_end_time, 1, 8) >= '20220215'\n" +
                        "        AND substr(refund_end_time, 1, 8) <= '20220814'\n" +
                        "    ) t1\n" +
                        "    JOIN (\n" +
                        "      SELECT\n" +
                        "        *\n" +
                        "      FROM\n" +
                        "        dim_itm_item_d\n" +
                        "      WHERE\n" +
                        "        dayid = '20220814'\n" +
                        "        AND diz_type LIKE '%old%'\n" +
                        "        AND category_1st_id != 610\n" +
                        "    ) t2 ON t1.item_id = t2.item_id\n" +
                        "  GROUP BY\n" +
                        "    t1.shop_id,\n" +
                        "    t2.brand_id\n" +
                        ")\n" +
                        "\n" +

                        "SELECT\n" +
                        "  main_shop_id,\n" +
                        "  shop_group_id AS group_id,\n" +
                        "  total_month3_cate10_gmv,\n" +
                        "  total_month6_cate10_gmv,\n" +
                        "  total_month3_cate_other_gmv,\n" +
                        "  total_month6_cate_other_gmv\n" +
                        "FROM\n" +
                        "  (\n" +
                        "    SELECT\n" +
                        "      parent_id AS main_shop_id,\n" +
                        "      collect_set(shop_group_id) [0] AS shop_group_id,\n" +
                        "      sum(nvl(month3_cate10_gmv, 0)) AS total_month3_cate10_gmv,\n" +
                        "      sum(nvl(month6_cate10_gmv, 0)) AS total_month6_cate10_gmv,\n" +
                        "      sum(nvl(month3_cate_other_gmv, 0)) AS total_month3_cate_other_gmv,\n" +
                        "      sum(nvl(month6_cate_other_gmv, 0)) AS total_month6_cate_other_gmv\n" +
                        "    FROM\n" +
                        "      (\n" +
                        "        SELECT\n" +
                        "          tmp1.parent_id,\n" +
                        "          tmp1.shop_group_id,\n" +
                        "          nvl(gmv_tmp.month3_cate10_gmv, 0) - nvl(refund_tmp.month3_cate10_refund_amt, 0) AS month3_cate10_gmv,\n" +
                        "          nvl(gmv_tmp.month6_cate10_gmv, 0) - nvl(refund_tmp.month6_cate10_refund_amt, 0) AS month6_cate10_gmv,\n" +
                        "          nvl(gmv_tmp.month3_cate_other_gmv, 0) - nvl(\n" +
                        "            refund_tmp.month3_cate_other_refund_amt,\n" +
                        "            0\n" +
                        "          ) AS month3_cate_other_gmv,\n" +
                        "          nvl(gmv_tmp.month6_cate_other_gmv, 0) - nvl(\n" +
                        "            refund_tmp.month6_cate_other_refund_amt,\n" +
                        "            0\n" +
                        "          ) AS month6_cate_other_gmv\n" +
                        "        FROM\n" +
                        "          (\n" +
                        "            SELECT\n" +
                        "              brand.id AS brand_id,\n" +
                        "              shop_gm.edit_time AS group_open_time,\n" +
                        "              shop_gm.group_id AS shop_group_id,\n" +
                        "              shop_base.shop_id,\n" +
                        "              shop_base.parent_id\n" +
                        "            FROM\n" +
                        "              (\n" +
                        "                SELECT\n" +
                        "                  *\n" +
                        "                FROM\n" +
                        "                  ytdw.dwd_shop_group_mapping_d\n" +
                        "                WHERE\n" +
                        "                  dayid = '20220814'\n" +
                        "                  AND substr(edit_time, 1, 8) < '20220514'\n" +
                        "                  AND is_deleted = 0\n" +
                        "              ) shop_gm\n" +
                        "              JOIN (\n" +
                        "                SELECT\n" +
                        "                  *\n" +
                        "                FROM\n" +
                        "                  ytdw.dwd_shop_group_d\n" +
                        "                WHERE\n" +
                        "                  dayid = '20220814'\n" +
                        "                  AND biz_type = 0\n" +
                        "              ) shop_group ON shop_gm.group_id = shop_group.id\n" +
                        "              JOIN (\n" +
                        "                SELECT\n" +
                        "                  *\n" +
                        "                FROM\n" +
                        "                  ytdw.dwd_brand_d\n" +
                        "                WHERE\n" +
                        "                  dayid = '20220814'\n" +
                        "              ) brand ON shop_group.id = brand.shop_group_id\n" +
                        "              JOIN (\n" +
                        "                SELECT\n" +
                        "                  *\n" +
                        "                FROM\n" +
                        "                  st_dkh_branch_chain_shop\n" +
                        "                WHERE\n" +
                        "                  dayid = '20220814'\n" +
                        "              ) shop_base ON shop_gm.shop_id = shop_base.shop_id\n" +
                        "          ) tmp1\n" +
                        "          LEFT JOIN gmv_tmp ON tmp1.shop_id = gmv_tmp.shop_id\n" +
                        "          AND tmp1.brand_id = gmv_tmp.brand_id\n" +
                        "          LEFT JOIN refund_tmp ON tmp1.shop_id = refund_tmp.shop_id\n" +
                        "          AND tmp1.brand_id = refund_tmp.brand_id\n" +
                        "      ) group_gmv\n" +
                        "    GROUP BY\n" +
                        "      parent_id,\n" +
                        "      shop_group_id\n" +
                        "  ) group_gmv_summary";

        //@formatter:on

        List<Tuple2<Set<String>, String>> lineage = getLineage(sql);
        Assert.assertEquals(lineage.size(), 1);
        Assert.assertEquals(lineage.get(0), Tuple(API.Set("ytdw.dwd_brand_d","dws_hpc_trd_shop_item_d", "st_dkh_branch_chain_shop", "dw_afs_order_refund_new_d", "ytdw.dwd_shop_group_d", "ytdw.dwd_shop_group_mapping_d", "dim_itm_item_d").toJavaSet(), ""));


        System.out.println(lineage);
    }
}
