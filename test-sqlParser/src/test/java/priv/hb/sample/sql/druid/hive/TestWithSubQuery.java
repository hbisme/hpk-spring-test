package priv.hb.sample.sql.druid.hive;

import priv.hb.sample.sql.druid.common.SqlAnalyze;

import org.junit.Test;

import java.util.List;

import io.vavr.Tuple2;

/**
 * @author hubin
 * @date 2022年09月19日 16:51
 */
public class TestWithSubQuery {
    @Test
    public void test1() {
        String sql =
                "with conversation_record AS \n" +
                        "    (SELECT user_id,\n" +
                        "        start_time,\n" +
                        "        time_length,\n" +
                        "         get_json_object(customer_json,\n" +
                        "         '$.shop_id') AS shop_id,call_type\n" +
                        "    FROM dw_hpc_sel_dx_conversation_record_d\n" +
                        "    WHERE dayid = '20220814'\n" +
                        "            AND nvl(get_json_object(customer_json, '$.shop_id'),'') != ''\n" +
                        "            AND conversation_type=1\n" +
                        "            AND user_id != '' ) \n" +

                        "insert overwrite table st_crm_conversation_accept_detail_d partition (dayid='20220814')\n" +
                        "SELECT all.user_id user_id,\n" +
                        "         all.time time,\n" +
                        "         user_real_name,\n" +
                        "         dept_name AS name,\n" +
                        "         '' job_grade, nvl(all_out_count, 0) all_out_count, nvl(all_out_accept_count, 0) all_out_accept_count, concat(round(nvl(all_out_accept_count, 0)/all_out_count, 4)*100, '%') all_out_accept_count_percent, nvl(all_in_count, 0) all_in_count, nvl(all_in_accept_count, 0) all_in_accept_count, concat(round(nvl(all_in_accept_count, 0)/all_in_count, 4)*100, '%') all_in_accept_count_percent, nvl(all_count, 0) all_count, concat(round(nvl(nvl(all_out_accept_count, 0)+nvl(all_in_accept_count, 0), 0)/all_count, 4)*100, '%') all_accept_count_percent, nvl(all_sum_out_time, 0) all_sum_out_time, nvl(all_sum_in_time, 0) all_sum_in_time, nvl(all_sum_time, 0) all_sum_time\n" +
                        "FROM \n" +
                        "    (SELECT user_id,\n" +
                        "         from_unixtime(unix_timestamp(start_time,\n" +
                        "         'yyyyMMddHHmmss'), 'yyyy-MM-dd') time, count (1) all_count, round(sum(time_length)/ 60, 2) all_sum_time\n" +
                        "    FROM conversation_record\n" +
                        "    WHERE substr(start_time, 1, 6) = '202208'\n" +
                        "    GROUP BY  user_id, from_unixtime(unix_timestamp(start_time, 'yyyyMMddHHmmss'), 'yyyy-MM-dd') ) ALL full outer\n" +
                        "JOIN \n" +
                        "    (SELECT user_id user_id1,\n" +
                        "         from_unixtime(unix_timestamp(start_time,\n" +
                        "         'yyyyMMddHHmmss'), 'yyyy-MM-dd') time1, count (1) all_out_count, round(sum(time_length) / 60, 2) all_sum_out_time\n" +
                        "    FROM conversation_record\n" +
                        "    WHERE substr(start_time, 1, 6) = '202208'\n" +
                        "            AND call_type IN (2, 4)\n" +
                        "    GROUP BY  user_id, from_unixtime(unix_timestamp(start_time, 'yyyyMMddHHmmss'), 'yyyy-MM-dd') ) out\n" +
                        "    ON all.user_id = out.user_id1\n" +
                        "        AND all.time = out.time1 full outer\n" +
                        "JOIN \n" +
                        "    (SELECT user_id user_id2,\n" +
                        "         from_unixtime(unix_timestamp(start_time,\n" +
                        "        'yyyyMMddHHmmss'),'yyyy-MM-dd') time2, count (1) all_out_accept_count\n" +
                        "    FROM conversation_record\n" +
                        "    WHERE substr(start_time, 1, 6) = '202208'\n" +
                        "            AND call_type IN (2, 4)\n" +
                        "            AND time_length > 0\n" +
                        "    GROUP BY  user_id, from_unixtime(unix_timestamp(start_time,'yyyyMMddHHmmss'),'yyyy-MM-dd') ) out_accept\n" +
                        "    ON all.user_id = out_accept.user_id2\n" +
                        "        AND all.time = out_accept.time2 full outer\n" +
                        "JOIN \n" +
                        "    (SELECT user_id user_id3,\n" +
                        "         from_unixtime(unix_timestamp(start_time,\n" +
                        "        'yyyyMMddHHmmss'),'yyyy-MM-dd') time3, count (1) all_in_count, round(sum(time_length) / 60, 2) all_sum_in_time\n" +
                        "    FROM conversation_record\n" +
                        "    WHERE substr(start_time, 1, 6) = '202208'\n" +
                        "            AND call_type IN (1, 3)\n" +
                        "    GROUP BY  user_id, from_unixtime(unix_timestamp(start_time,'yyyyMMddHHmmss'),'yyyy-MM-dd') ) call_in\n" +
                        "    ON all.user_id = call_in.user_id3\n" +
                        "        AND all.time = call_in.time3 full outer\n" +
                        "JOIN \n" +
                        "    (SELECT user_id user_id4,\n" +
                        "         from_unixtime(unix_timestamp(start_time,\n" +
                        "        'yyyyMMddHHmmss'),'yyyy-MM-dd') time4, count (1) all_in_accept_count\n" +
                        "    FROM conversation_record\n" +
                        "    WHERE substr(start_time, 1, 6) = '202208'\n" +
                        "            AND call_type IN (1, 3)\n" +
                        "    GROUP BY  user_id, from_unixtime(unix_timestamp(start_time,'yyyyMMddHHmmss'),'yyyy-MM-dd') ) call_in_accept\n" +
                        "    ON all.user_id = call_in_accept.user_id4\n" +
                        "        AND all.time = call_in_accept.time4\n" +
                        "LEFT JOIN dim_hpc_pub_user_admin useradmin\n" +
                        "    ON all.user_id = useradmin.user_id\n" +
                        "WHERE useradmin.user_id is NOT null\n" +
                        "        AND useradmin.dept_id is NOT null";

        Tuple2<List<String>, String> sourceTableTargetTable = SqlAnalyze.extractTableFromWithQuery(sql);
        System.out.println(sourceTableTargetTable);

    }


    @Test
    public void test2() {
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
                        "  ytdw.hive_info\n" +
                        "  cross join hdfs_info";

        Tuple2<List<String>, String> sourceTableTargetTable = SqlAnalyze.extractTableFromWithQuery(sql);
        System.out.println(sourceTableTargetTable);
    }

    @Test
    public void test3() {
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

        Tuple2<List<String>, String> sourceTableTargetTable = SqlAnalyze.extractTableFromWithQuery(sql);
        System.out.println(sourceTableTargetTable);
    }


    @Test
    public void test4() {
        String sql =
                "with shop_brand_preference AS (\n" +
                        "  SELECT\n" +
                        "    shop_id,\n" +
                        "    collect_set(brand_id) AS brand_preference\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        shop_id,\n" +
                        "        brand_id,\n" +
                        "        gmv,\n" +
                        "        row_number() over(\n" +
                        "          partition by shop_id\n" +
                        "          ORDER BY\n" +
                        "            gmv desc\n" +
                        "        ) rn\n" +
                        "      FROM\n" +
                        "        (\n" +
                        "          SELECT\n" +
                        "            shop_id,\n" +
                        "            brand_id,\n" +
                        "            gmv *(all_gmv / brand_gmv) AS gmv\n" +
                        "          FROM\n" +
                        "            (\n" +
                        "              SELECT\n" +
                        "                shop_id,\n" +
                        "                brand_id,\n" +
                        "                gmv,\n" +
                        "                sum(gmv) over(partition by brand_id) brand_gmv,\n" +
                        "                sum(gmv) over() all_gmv\n" +
                        "              FROM\n" +
                        "                (\n" +
                        "                  SELECT\n" +
                        "                    shop_id,\n" +
                        "                    brand_id,\n" +
                        "                    cast(\n" +
                        "                      sum(gmv *(1 /(diffdays + 1))) AS decimal(30, 2)\n" +
                        "                    ) AS gmv\n" +
                        "                  FROM\n" +
                        "                    (\n" +
                        "                      SELECT\n" +
                        "                        shop_id,\n" +
                        "                        brand_id,\n" +
                        "                        datediff(\n" +
                        "                          current_date,\n" +
                        "                          from_unixtime(\n" +
                        "                            unix_timestamp(date_id, 'yyyyMMdd'),\n" +
                        "                            'yyyy-MM-dd'\n" +
                        "                          )\n" +
                        "                        ) AS diffdays,\n" +
                        "                        sum(pay_total_amt_1d) AS gmv\n" +
                        "                      FROM\n" +
                        "                        ytdw.dws_hpc_trd_detail_d\n" +
                        "                      WHERE\n" +
                        "                        dayid = '20220820'\n" +
                        "                        AND is_pay_bp = 1\n" +
                        "                        AND date_id <= '20220820'\n" +
                        "                      GROUP BY\n" +
                        "                        shop_id,\n" +
                        "                        brand_id,\n" +
                        "                        date_id\n" +
                        "                    ) AS t1\n" +
                        "                  GROUP BY\n" +
                        "                    shop_id,\n" +
                        "                    brand_id\n" +
                        "                ) AS t2\n" +
                        "            ) AS t3\n" +
                        "        ) AS t4\n" +
                        "    ) AS t5\n" +
                        "  GROUP BY\n" +
                        "    shop_id\n" +
                        "),\n" +
                        "\n" +

                        "shop_price_sensitivity AS (\n" +
                        "  SELECT\n" +
                        "    shop_id,\n" +
                        "    sum(\n" +
                        "      if(\n" +
                        "        (shop_item_pay_amount / shop_item_item_count) /(item_pay_amount / item_item_count) < 1,\n" +
                        "        1,\n" +
                        "        0\n" +
                        "      )\n" +
                        "    ) / sum(1) AS price_sensitivity\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        pay_date,\n" +
                        "        shop_id,\n" +
                        "        item_id,\n" +
                        "        sum(pay_amount) over(\n" +
                        "          distribute by shop_id,\n" +
                        "          item_id sort by pay_date rows BETWEEN 27 PRECEDING\n" +
                        "          AND CURRENT ROW\n" +
                        "        ) AS shop_item_pay_amount,\n" +
                        "        sum(pay_amount) over(\n" +
                        "          distribute by item_id sort by pay_date rows BETWEEN 27 PRECEDING\n" +
                        "          AND CURRENT ROW\n" +
                        "        ) AS item_pay_amount,\n" +
                        "        sum(item_count) over(\n" +
                        "          distribute by shop_id,\n" +
                        "          item_id sort by pay_date rows BETWEEN 27 PRECEDING\n" +
                        "          AND CURRENT ROW\n" +
                        "        ) AS shop_item_item_count,\n" +
                        "        sum(item_count) over(\n" +
                        "          distribute by item_id sort by pay_date rows BETWEEN 27 PRECEDING\n" +
                        "          AND CURRENT ROW\n" +
                        "        ) AS item_item_count\n" +
                        "      FROM\n" +
                        "        (\n" +
                        "          SELECT\n" +
                        "            date_id AS pay_date,\n" +
                        "            shop_id,\n" +
                        "            item_id,\n" +
                        "            sum(pay_amt_1d) AS pay_amount,\n" +
                        "            sum(pay_itm_unit_1d) AS item_count\n" +
                        "          FROM\n" +
                        "            ytdw.dws_hpc_trd_detail_d\n" +
                        "          WHERE\n" +
                        "            dayid = '20220820'\n" +
                        "            AND is_pay_bp = 1\n" +
                        "            AND date_id > '20220221'\n" +
                        "            AND pay_amt_1d > 0\n" +
                        "            AND pay_itm_unit_1d > 0\n" +
                        "          GROUP BY\n" +
                        "            1,\n" +
                        "            2,\n" +
                        "            3\n" +
                        "        ) AS t1\n" +
                        "    ) AS t2\n" +
                        "  GROUP BY\n" +
                        "    1\n" +
                        "),\n" +
                        "coupon_dept AS (\n" +
                        "  SELECT\n" +
                        "    order_shop.order_id,\n" +
                        "    coupon_owner.platform_coupon_fee_belong_virtual_1st_dept_id AS coupon_1st_dept_id,\n" +
                        "    coupon_owner.platform_coupon_fee_belong_virtual_1st_dept_name AS coupon_1st_dept_name,\n" +
                        "    coupon_owner.platform_coupon_fee_belong_virtual_2nd_dept_id AS coupon_2nd_dept_id,\n" +
                        "    coupon_owner.platform_coupon_fee_belong_virtual_2nd_dept_name AS coupon_2nd_dept_name\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        order_id,\n" +
                        "        coupon_owner_id\n" +
                        "      FROM\n" +
                        "        ytdw.dwd_order_shop_d\n" +
                        "      WHERE\n" +
                        "        dayid = '20220820'\n" +
                        "        AND substr(pay_time, 1, 8) >= '20220522'\n" +
                        "        AND substr(pay_time, 1, 8) <= '20220820'\n" +
                        "    ) AS order_shop\n" +
                        "    LEFT JOIN (\n" +
                        "      SELECT\n" +
                        "        coupon_owner_id,\n" +
                        "        platform_coupon_fee_belong_virtual_1st_dept_id,\n" +
                        "        platform_coupon_fee_belong_virtual_1st_dept_name,\n" +
                        "        platform_coupon_fee_belong_virtual_2nd_dept_id,\n" +
                        "        platform_coupon_fee_belong_virtual_2nd_dept_name\n" +
                        "      FROM\n" +
                        "        ytdw.dw_hpc_prm_coupon_owner_d\n" +
                        "      WHERE\n" +
                        "        dayid = '20220820'\n" +
                        "    ) AS coupon_owner ON coupon_owner.coupon_owner_id = order_shop.coupon_owner_id\n" +
                        "),\n" +
                        "\n" +

                        "prm_date_item AS (\n" +
                        "  SELECT\n" +
                        "    item_id,\n" +
                        "    date_id,\n" +
                        "    promotion_date_type_ids,\n" +
                        "    promotion_date_type_abs,\n" +
                        "    promotion_date_type_names\n" +
                        "  FROM\n" +
                        "    ytdw.dw_hpc_prm_promotion_date_item_di\n" +
                        "  WHERE\n" +
                        "    dayid >= '20220522'\n" +
                        "    AND dayid <= '20220820'\n" +
                        "),\n" +
                        "shop_platform_active AS (\n" +
                        "  SELECT\n" +
                        "    com_trd.shop_id,\n" +
                        "    concat_ws(\n" +
                        "      ',',\n" +
                        "      collect_set(\n" +
                        "        CASE\n" +
                        "          WHEN prm_date_item.promotion_date_type_ids = '8'\n" +
                        "          AND coupon_dept.coupon_2nd_dept_id IN ('218', '219')\n" +
                        "          AND com_trd.pay_platform_coupon_amt_1d > 0 THEN '品类日'\n" +
                        "          WHEN prm_date_item.promotion_date_type_ids = '9'\n" +
                        "          AND coupon_dept.coupon_2nd_dept_id IN ('218', '219')\n" +
                        "          AND com_trd.pay_platform_coupon_amt_1d > 0 THEN '品牌日'\n" +
                        "          WHEN prm_date_item.promotion_date_type_abs = 'B类' THEN 'B促'\n" +
                        "          WHEN prm_date_item.promotion_date_type_abs = 'A类' THEN 'A促'\n" +
                        "          ELSE null\n" +
                        "        END\n" +
                        "      )\n" +
                        "    ) AS shop_platform_active_preference\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        date_id,\n" +
                        "        trade_no,\n" +
                        "        order_id,\n" +
                        "        shop_id,\n" +
                        "        shop_name,\n" +
                        "        item_id,\n" +
                        "        item_name,\n" +
                        "        pay_total_amt_1d,\n" +
                        "        pay_platform_coupon_amt_1d,\n" +
                        "        is_com_pay_bp,\n" +
                        "        is_com_rfd_bp\n" +
                        "      FROM\n" +
                        "        ytdw.dws_hpc_trd_com_detail_d\n" +
                        "      WHERE\n" +
                        "        dayid = '20220820'\n" +
                        "        AND date_id >= '20220522'\n" +
                        "        AND date_id <= '20220820'\n" +
                        "        AND is_com_pay_bp = 1\n" +
                        "    ) AS com_trd\n" +
                        "    LEFT JOIN coupon_dept ON coupon_dept.order_id = com_trd.order_id\n" +
                        "    LEFT JOIN prm_date_item ON prm_date_item.item_id = com_trd.item_id\n" +
                        "    AND prm_date_item.date_id = com_trd.date_id\n" +
                        "  GROUP BY\n" +
                        "    com_trd.shop_id\n" +
                        ")\n" +
                        "\n" +

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

        Tuple2<List<String>, String> sourceTableTargetTable = SqlAnalyze.extractTableFromWithQuery(sql);
        System.out.println(sourceTableTargetTable);
    }

    @Test
    public void test5() {
        String sql =
                "WITH shop AS \n" +
                        "    (SELECT shop_id,\n" +
                        "         shop_area_id\n" +
                        "    FROM ytdw.dim_hpc_shp_shop_d\n" +
                        "    WHERE dayid='20220415'\n" +
                        "            AND shop_status <>'6'\n" +
                        "            AND shop_is_inuse =1) \n" +

                        "INSERT overwrite directory '/tmp/dim_hpc_itm_item_temp' \n" +
                        "SELECT s.shop_id,\n" +
                        "        i.item_id\n" +
                        "FROM dw_shop_d s\n" +
                        "LEFT JOIN ytdw.dws_item_d i";

        Tuple2<List<String>, String> sourceTableTargetTable = SqlAnalyze.extractTableFromWithQuery(sql);
        System.out.println(sourceTableTargetTable);
    }

    @Test
    public void test6() {
        String sql = "WITH shop AS (\n" +
                "  SELECT\n" +
                "    shop_id,\n" +
                "    shop_area_id\n" +
                "  FROM\n" +
                "    ytdw.dim_hpc_shp_shop_d\n" +
                "  WHERE\n" +
                "    dayid = '20220415'\n" +
                "    AND shop_status <> '6'\n" +
                "    AND shop_is_inuse = 1\n" +
                "),\n" +
                "\n" +

                "cate3_data AS (\n" +
                "  SELECT\n" +
                "    t2.item_id,\n" +
                "    t3.shop_id,\n" +
                "    t3.cnt * t2.sum_popularity AS popularity\n" +
                "  FROM\n" +
                "    (\n" +
                "      SELECT\n" +
                "        item_id,\n" +
                "        shop_area_id,\n" +
                "        category_leaf_id,\n" +
                "        sum_popularity\n" +
                "      FROM\n" +
                "        (\n" +
                "          SELECT\n" +
                "            t.item_id,\n" +
                "            shop_area_id,\n" +
                "            item.category_leaf_id,\n" +
                "            sum_popularity,\n" +
                "            row_number() over(\n" +
                "              partition BY item.category_leaf_id,\n" +
                "              shop_area_id\n" +
                "              ORDER BY\n" +
                "                sum_popularity DESC\n" +
                "            ) AS rn\n" +
                "          FROM\n" +
                "            (\n" +
                "              SELECT\n" +
                "                item_id,\n" +
                "                sum(popularity) AS sum_popularity,\n" +
                "                shop_area_id\n" +
                "              FROM\n" +
                "                (\n" +
                "                  SELECT\n" +
                "                    item_id,\n" +
                "                    shop_id,\n" +
                "                    popularity\n" +
                "                  FROM\n" +
                "                    reco.dw_shop_item_popularity_d\n" +
                "                  WHERE\n" +
                "                    dayid = '20220415'\n" +
                "                    AND shop_id IS NOT NULL\n" +
                "                ) popu\n" +
                "                LEFT JOIN shop ON popu.shop_id = shop.shop_id\n" +
                "              GROUP BY\n" +
                "                item_id,\n" +
                "                shop_area_id\n" +
                "            ) t\n" +
                "            JOIN (\n" +
                "              SELECT\n" +
                "                item_id,\n" +
                "                category_leaf_id\n" +
                "              FROM\n" +
                "                reco.dwd_alg_valid_item_d\n" +
                "              WHERE\n" +
                "                dayid = '20220415'\n" +
                "            ) item ON t.item_id = item.item_id\n" +
                "        ) t1\n" +
                "      WHERE\n" +
                "        rn < 10\n" +
                "    ) t2\n" +
                "    JOIN (\n" +
                "      SELECT\n" +
                "        ord_item.shop_id,\n" +
                "        ord_item.category_leaf_id,\n" +
                "        ord_item.cnt,\n" +
                "        shop.shop_area_id\n" +
                "      FROM\n" +
                "        (\n" +
                "          SELECT\n" +
                "            ord.shop_id,\n" +
                "            item.category_leaf_id,\n" +
                "            count(1) AS cnt\n" +
                "          FROM\n" +
                "            (\n" +
                "              SELECT\n" +
                "                shop_id,\n" +
                "                item_id\n" +
                "              FROM\n" +
                "                ytdw.dws_hpc_trd_com_detail_d\n" +
                "              WHERE\n" +
                "                dayid = '20220415'\n" +
                "                AND date_id > '20220115'\n" +
                "                AND date_id <= '20220415'\n" +
                "            ) ord\n" +
                "            JOIN (\n" +
                "              SELECT\n" +
                "                item_id,\n" +
                "                category_leaf_id\n" +
                "              FROM\n" +
                "                reco.dwd_alg_valid_item_d\n" +
                "              WHERE\n" +
                "                dayid = '20220415'\n" +
                "            ) item ON ord.item_id = item.item_id\n" +
                "          GROUP BY\n" +
                "            ord.shop_id,\n" +
                "            item.category_leaf_id\n" +
                "        ) ord_item\n" +
                "        LEFT JOIN shop ON shop.shop_id = ord_item.shop_id\n" +
                "    ) t3 ON t3.category_leaf_id = t2.category_leaf_id\n" +
                "    AND t3.shop_area_id = t2.shop_area_id\n" +
                "),\n" +
                "\n" +

                "cate3_recall AS (\n" +
                "  SELECT\n" +
                "    shop_id,\n" +
                "    item_id,\n" +
                "    original_score,\n" +
                "    (\n" +
                "      sum_category_leaf_id_cnt2 - min(sum_category_leaf_id_cnt2) over()\n" +
                "    ) /(\n" +
                "      max(sum_category_leaf_id_cnt2) over() - min(sum_category_leaf_id_cnt2) over()\n" +
                "    ) AS recall_score\n" +
                "  FROM\n" +
                "    (\n" +
                "      SELECT\n" +
                "        shop_id,\n" +
                "        item_id,\n" +
                "        popularity AS original_score,\n" +
                "        log10(popularity + 1) AS sum_category_leaf_id_cnt2\n" +
                "      FROM\n" +
                "        cate3_data\n" +
                "    ) t1\n" +
                "),\n" +
                "\n" +

                "cate3_recall_re AS (\n" +
                "  SELECT\n" +
                "    shop_id,\n" +
                "    item_id,\n" +
                "    original_score,\n" +
                "    recall_score,\n" +
                "    avg_recall_score,\n" +
                "    std_recall_score,\n" +
                "    std_recall_score / avg_recall_score AS variation_coefficient\n" +
                "  FROM\n" +
                "    (\n" +
                "      SELECT\n" +
                "        shop_id,\n" +
                "        item_id,\n" +
                "        original_score,\n" +
                "        recall_score,\n" +
                "        avg_recall_score,\n" +
                "        if(\n" +
                "          cnt_flag = 1,\n" +
                "          sqrt(\n" +
                "            (\n" +
                "              (\n" +
                "                sum(pow((recall_score - avg_recall_score), 2)) over()\n" +
                "              ) + (30000000 - cnt) * pow(avg_recall_score, 2)\n" +
                "            ) / 30000000\n" +
                "          ),\n" +
                "          sqrt(\n" +
                "            (\n" +
                "              sum(pow((recall_score - avg_recall_score), 2)) over()\n" +
                "            ) / cnt\n" +
                "          )\n" +
                "        ) AS std_recall_score\n" +
                "      FROM\n" +
                "        (\n" +
                "          SELECT\n" +
                "            shop_id,\n" +
                "            item_id,\n" +
                "            original_score,\n" +
                "            recall_score,\n" +
                "            cnt,\n" +
                "            cnt_flag,\n" +
                "            if(\n" +
                "              cnt_flag = 1,\n" +
                "              sum(recall_score) over() / 30000000,\n" +
                "              sum(recall_score) over() / cnt\n" +
                "            ) AS avg_recall_score\n" +
                "          FROM\n" +
                "            (\n" +
                "              SELECT\n" +
                "                shop_id,\n" +
                "                item_id,\n" +
                "                original_score,\n" +
                "                recall_score,\n" +
                "                count(1) over() AS cnt,\n" +
                "                if(\n" +
                "                  30000000 - count(1) over() > 0,\n" +
                "                  1,\n" +
                "                  0\n" +
                "                ) AS cnt_flag\n" +
                "              FROM\n" +
                "                cate3_recall\n" +
                "            ) t\n" +
                "        ) t1\n" +
                "    ) t2\n" +
                ")\n" +
                "\n" +

                "INSERT overwrite TABLE reco.dws_shop_item_recall_d partition (dayid = '20220415', recall_type = '近期下单三级类目召回')\n" +
                "SELECT\n" +
                "  shop_id,\n" +
                "  item_id,\n" +
                "  original_score,\n" +
                "  recall_score,\n" +
                "  variation_coefficient,\n" +
                "  '平台行为' AS subject_domain\n" +
                "FROM\n" +
                "  cate3_recall_re";

        Tuple2<List<String>, String> sourceTableTargetTable = SqlAnalyze.extractTableFromWithQuery(sql);
        System.out.println(sourceTableTargetTable);
    }

    @Test
    public void test7() {
        String sql =
                "with supply_status_is_StopCope AS (\n" +
                        "  SELECT\n" +
                        "    supply_id AS supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN supply_status = 10 THEN 1\n" +
                        "        ELSE ''\n" +
                        "      END\n" +
                        "    ) AS supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        supply_id,\n" +
                        "        supply_status AS supply_status\n" +
                        "      FROM\n" +
                        "        dim_ytj_sup_supply_d\n" +
                        "      WHERE\n" +
                        "        dayid = '20220430'\n" +
                        "        AND supply_id is NOT NULL\n" +
                        "    ) t1\n" +
                        "),\n" +
                        "\n" +

                        "pay_total_amt_6m AS (\n" +
                        "  SELECT\n" +
                        "    t2.supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN t2.sum_pay_total_amt <= 10 THEN 2\n" +
                        "        ELSE ''\n" +
                        "      END\n" +
                        "    ) AS supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        supply_id,\n" +
                        "        sum(pay_total_amt) AS sum_pay_total_amt,\n" +
                        "        count(order_id) AS count_order_id\n" +
                        "      FROM\n" +
                        "        dw_hpc_trd_com_ord_d\n" +
                        "      WHERE\n" +
                        "        dayid = '20220430'\n" +
                        "        AND datediff(\n" +
                        "          from_unixtime(unix_timestamp(dayid, 'yyyyMMdd'), 'yyyy-MM-dd'),\n" +
                        "          from_unixtime(\n" +
                        "            unix_timestamp(substr(pay_time, 0, 8), 'yyyyMMdd'),\n" +
                        "            'yyyy-MM-dd'\n" +
                        "          )\n" +
                        "        ) <= 180\n" +
                        "        AND supply_id is NOT null\n" +
                        "      GROUP BY\n" +
                        "        supply_id\n" +
                        "    ) t2\n" +
                        "),\n" +
                        "\n" +

                        "pay_total_amt_1y AS (\n" +
                        "  SELECT\n" +
                        "    t3.supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN t3.sum_pay_total_amt <= 30 THEN 3\n" +
                        "        ELSE ''\n" +
                        "      END\n" +
                        "    ) as supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        supply_id,\n" +
                        "        sum(pay_total_amt) AS sum_pay_total_amt\n" +
                        "      FROM\n" +
                        "        dw_hpc_trd_com_ord_d\n" +
                        "      WHERE\n" +
                        "        dayid = '20220430'\n" +
                        "        AND datediff(\n" +
                        "          from_unixtime(unix_timestamp(dayid, 'yyyyMMdd'), 'yyyy-MM-dd'),\n" +
                        "          from_unixtime(\n" +
                        "            unix_timestamp(substr(pay_time, 0, 8), 'yyyyMMdd'),\n" +
                        "            'yyyy-MM-dd'\n" +
                        "          )\n" +
                        "        ) <= 365\n" +
                        "        AND supply_id is NOT null\n" +
                        "      GROUP BY\n" +
                        "        supply_id\n" +
                        "    ) t3\n" +
                        "),\n" +
                        "\n" +

                        "pay_total_amt_ratio_1y_2y AS (\n" +
                        "  SELECT\n" +
                        "    t6.supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN t6.ratio <= 0.2 THEN 4\n" +
                        "        ELSE ''\n" +
                        "      END\n" +
                        "    ) as supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        t4.supply_id,\n" +
                        "        (t4.tota1 / (t5.tota2 - t4.tota1)) AS ratio\n" +
                        "      FROM\n" +
                        "        (\n" +
                        "          SELECT\n" +
                        "            supply_id,\n" +
                        "            sum(pay_total_amt) AS tota1\n" +
                        "          FROM\n" +
                        "            dw_hpc_trd_com_ord_d\n" +
                        "          WHERE\n" +
                        "            dayid = '20220430'\n" +
                        "            AND datediff(\n" +
                        "              from_unixtime(unix_timestamp(dayid, 'yyyyMMdd'), 'yyyy-MM-dd'),\n" +
                        "              from_unixtime(\n" +
                        "                unix_timestamp(substr(pay_time, 1, 8), 'yyyyMMdd'),\n" +
                        "                'yyyy-MM-dd'\n" +
                        "              )\n" +
                        "            ) <= 365\n" +
                        "          GROUP BY\n" +
                        "            supply_id\n" +
                        "        ) t4\n" +
                        "        LEFT JOIN (\n" +
                        "          SELECT\n" +
                        "            supply_id,\n" +
                        "            sum(pay_total_amt) AS tota2\n" +
                        "          FROM\n" +
                        "            dw_hpc_trd_com_ord_d\n" +
                        "          WHERE\n" +
                        "            dayid = '20220430'\n" +
                        "            AND datediff(\n" +
                        "              from_unixtime(unix_timestamp(dayid, 'yyyyMMdd'), 'yyyy-MM-dd'),\n" +
                        "              from_unixtime(\n" +
                        "                unix_timestamp(substr(pay_time, 1, 8), 'yyyyMMdd'),\n" +
                        "                'yyyy-MM-dd'\n" +
                        "              )\n" +
                        "            ) <= 730\n" +
                        "          GROUP BY\n" +
                        "            supply_id\n" +
                        "        ) t5 ON t4.supply_id = t5.supply_id\n" +
                        "      WHERE\n" +
                        "        t4.supply_id is NOT null\n" +
                        "        AND t5.supply_id is NOT NULL\n" +
                        "    ) t6\n" +
                        "),\n" +
                        "\n" +

                        "pay_trd_cnt_6m AS (\n" +
                        "  SELECT\n" +
                        "    t7.supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN t7.count_order_id <= 1 THEN 5\n" +
                        "        ELSE ''\n" +
                        "      END\n" +
                        "    ) as supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        supply_id,\n" +
                        "        count(order_id) AS count_order_id\n" +
                        "      FROM\n" +
                        "        dw_hpc_trd_com_ord_d\n" +
                        "      WHERE\n" +
                        "        dayid = '20220430'\n" +
                        "        AND datediff(\n" +
                        "          from_unixtime(unix_timestamp(dayid, 'yyyyMMdd'), 'yyyy-MM-dd'),\n" +
                        "          from_unixtime(\n" +
                        "            unix_timestamp(substr(pay_time, 1, 8), 'yyyyMMdd'),\n" +
                        "            'yyyy-MM-dd'\n" +
                        "          )\n" +
                        "        ) <= 180\n" +
                        "        AND supply_id is NOT null\n" +
                        "        AND order_id is NOT null\n" +
                        "      GROUP BY\n" +
                        "        supply_id\n" +
                        "    ) t7\n" +
                        "),\n" +
                        "\n" +

                        "pay_trd_cnt_1y_sum_pay_total_amt_1y AS (\n" +
                        "  SELECT\n" +
                        "    t8.supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN (\n" +
                        "          count_order_id <= 5\n" +
                        "          AND sum_pay_total_amt <= 50\n" +
                        "        ) THEN 6\n" +
                        "        ELSE ''\n" +
                        "      END\n" +
                        "    ) AS supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        supply_id,\n" +
                        "        count(order_id) AS count_order_id,\n" +
                        "        sum(pay_total_amt) AS sum_pay_total_amt\n" +
                        "      FROM\n" +
                        "        dw_hpc_trd_com_ord_d\n" +
                        "      WHERE\n" +
                        "        dayid = '20220430'\n" +
                        "        AND datediff(\n" +
                        "          from_unixtime(unix_timestamp(dayid, 'yyyyMMdd'), 'yyyy-MM-dd'),\n" +
                        "          from_unixtime(\n" +
                        "            unix_timestamp(substr(pay_time, 1, 8), 'yyyyMMdd'),\n" +
                        "            'yyyy-MM-dd'\n" +
                        "          )\n" +
                        "        ) <= 365\n" +
                        "      GROUP BY\n" +
                        "        supply_id\n" +
                        "    ) t8\n" +
                        "),\n" +
                        "\n" +

                        "last_order_days_cnt AS (\n" +
                        "  SELECT\n" +
                        "    t9.supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN max_pay_time > 90 THEN 7\n" +
                        "        ELSE ''\n" +
                        "      END\n" +
                        "    ) AS supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        supply_id,\n" +
                        "        datediff(\n" +
                        "          from_unixtime(\n" +
                        "            unix_timestamp('20220430', 'yyyyMMdd'),\n" +
                        "            'yyyy-MM-dd'\n" +
                        "          ),\n" +
                        "          from_unixtime(\n" +
                        "            unix_timestamp(substr(pay_time, 1, 8), 'yyyyMMdd'),\n" +
                        "            'yyyy-MM-dd'\n" +
                        "          )\n" +
                        "        ) AS max_pay_time\n" +
                        "      FROM\n" +
                        "        (\n" +
                        "          SELECT\n" +
                        "            supply_id,\n" +
                        "            max(pay_time) AS pay_time\n" +
                        "          FROM\n" +
                        "            dw_hpc_trd_com_ord_d\n" +
                        "          WHERE\n" +
                        "            dayid = '20220430'\n" +
                        "          GROUP BY\n" +
                        "            supply_id\n" +
                        "        ) e2\n" +
                        "    ) t9\n" +
                        "),\n" +
                        "\n" +

                        "sup_frez_cnt_ratio_1y AS (\n" +
                        "  SELECT\n" +
                        "    tt1.supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN tt1.frez_cnt >= 5 THEN 8\n" +
                        "        ELSE ''\n" +
                        "      END\n" +
                        "    ) AS supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        supply_id,\n" +
                        "        count(1) AS frez_cnt\n" +
                        "      FROM\n" +
                        "        (\n" +
                        "          SELECT\n" +
                        "            supplier_id AS supply_id,\n" +
                        "            opt_type\n" +
                        "          FROM\n" +
                        "            dwd_supplier_operation_d\n" +
                        "          WHERE\n" +
                        "            opt_type = 9\n" +
                        "            AND dayid = '20220430'\n" +
                        "            AND datediff(\n" +
                        "              from_unixtime(\n" +
                        "                unix_timestamp('20220430', 'yyyyMMdd'),\n" +
                        "                'yyyy-MM-dd'\n" +
                        "              ),\n" +
                        "              from_unixtime(\n" +
                        "                unix_timestamp(substr(opt_time, 1, 8), 'yyyyMMdd'),\n" +
                        "                'yyyy-MM-dd'\n" +
                        "              )\n" +
                        "            ) <= 365\n" +
                        "        ) t10\n" +
                        "      GROUP BY\n" +
                        "        supply_id\n" +
                        "    ) tt1\n" +
                        "),\n" +
                        "\n" +

                        "trace_type_cnt_3m AS (\n" +
                        "  SELECT\n" +
                        "    tt4.supply_id AS supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN tt4.trace_type_cnt <= 10 THEN 9\n" +
                        "        ELSE ''\n" +
                        "      END\n" +
                        "    ) AS supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        tt3.supply_id,\n" +
                        "        count(1) trace_type_cnt\n" +
                        "      FROM\n" +
                        "        dw_hpc_flw_page_exposure_click_di tt2\n" +
                        "        LEFT JOIN (\n" +
                        "          SELECT\n" +
                        "            distinct(supply_user_id) AS user_id,\n" +
                        "            supply_id\n" +
                        "          FROM\n" +
                        "            dws_hpc_flw_supply_device_login_d\n" +
                        "          WHERE\n" +
                        "            dayid = '20220430'\n" +
                        "            AND supply_user_id <> ''\n" +
                        "            AND supply_user_id is NOT NULL\n" +
                        "        ) tt3 ON tt2.user_id = tt3.user_id\n" +
                        "        AND tt3.user_id is NOT null\n" +
                        "      WHERE\n" +
                        "        dayid >= date_sub(\n" +
                        "          from_unixtime(\n" +
                        "            unix_timestamp('20220430', 'yyyyMMdd'),\n" +
                        "            'yyyy-MM-dd'\n" +
                        "          ),\n" +
                        "          90\n" +
                        "        )\n" +
                        "        AND tt2.user_id <> ''\n" +
                        "        AND tt2.user_id is NOT null\n" +
                        "      GROUP BY\n" +
                        "        tt3.supply_id\n" +
                        "    ) tt4\n" +
                        "),\n" +
                        "\n" +

                        "sup_service_behind_trace_type_cnt_3m AS (\n" +
                        "  SELECT\n" +
                        "    tt7.supply_id AS supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN tt7.trace_type_cnt <= 10 THEN 10\n" +
                        "        ELSE ''\n" +
                        "      END\n" +
                        "    ) AS supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        tt6.supply_id,\n" +
                        "        count(1) AS trace_type_cnt\n" +
                        "      FROM\n" +
                        "        dw_hpc_flw_page_exposure_click_di tt5\n" +
                        "        LEFT JOIN (\n" +
                        "          SELECT\n" +
                        "            distinct(supply_user_id) AS user_id,\n" +
                        "            supply_id\n" +
                        "          FROM\n" +
                        "            dws_hpc_flw_supply_device_login_d\n" +
                        "          WHERE\n" +
                        "            dayid = '20220430'\n" +
                        "            AND supply_user_id <> ''\n" +
                        "            AND supply_user_id is NOT NULL\n" +
                        "        ) tt6 ON tt5.user_id = tt6.user_id\n" +
                        "        AND tt6.user_id is NOT null\n" +
                        "      WHERE\n" +
                        "        dayid >= date_sub(\n" +
                        "          from_unixtime(\n" +
                        "            unix_timestamp('20220430', 'yyyyMMdd'),\n" +
                        "            'yyyy-MM-dd'\n" +
                        "          ),\n" +
                        "          90\n" +
                        "        )\n" +
                        "        AND tt5.user_id <> ''\n" +
                        "        AND tt5.user_id is NOT null\n" +
                        "        AND tt5.code_application_name = '供应商服务后台【勿新增】'\n" +
                        "      GROUP BY\n" +
                        "        tt6.supply_id\n" +
                        "    ) tt7\n" +
                        "),\n" +
                        "\n" +

                        "sup_shop_url_trace_type_cnt_3m AS (\n" +
                        "  SELECT\n" +
                        "    a1.supply_id AS supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN a1.trace_type_cnt <= 10 THEN 11\n" +
                        "        ELSE ''\n" +
                        "      END\n" +
                        "    ) AS supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        tt9.supply_id,\n" +
                        "        count(1) trace_type_cnt\n" +
                        "      FROM\n" +
                        "        dw_hpc_flw_page_exposure_click_di tt8\n" +
                        "        LEFT JOIN (\n" +
                        "          SELECT\n" +
                        "            distinct(supply_user_id) AS user_id,\n" +
                        "            supply_id\n" +
                        "          FROM\n" +
                        "            dws_hpc_flw_supply_device_login_d\n" +
                        "          WHERE\n" +
                        "            dayid = '20220430'\n" +
                        "            AND supply_user_id <> ''\n" +
                        "            AND supply_user_id is NOT NULL\n" +
                        "        ) tt9 ON tt8.user_id = tt9.user_id\n" +
                        "        AND tt9.user_id is NOT null\n" +
                        "      WHERE\n" +
                        "        dayid >= date_sub(\n" +
                        "          from_unixtime(\n" +
                        "            unix_timestamp('20220430', 'yyyyMMdd'),\n" +
                        "            'yyyy-MM-dd'\n" +
                        "          ),\n" +
                        "          90\n" +
                        "        )\n" +
                        "        AND tt8.user_id <> ''\n" +
                        "        AND tt8.user_id is NOT null\n" +
                        "        AND tt8.code_application_name = '供应商商家端'\n" +
                        "      GROUP BY\n" +
                        "        tt9.supply_id\n" +
                        "    ) a1\n" +
                        "),\n" +
                        "\n" +

                        "req_cnt_90d_cnt AS (\n" +
                        "  SELECT\n" +
                        "    a2.supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN a2.sum_req_cnt_90d <= 10 THEN 12\n" +
                        "        ELSE ''\n" +
                        "      end\n" +
                        "    ) AS supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        supply_id,\n" +
                        "        sum(req_cnt_90d) AS sum_req_cnt_90d\n" +
                        "      FROM\n" +
                        "        dws_hpc_flw_supply_device_login_d\n" +
                        "      WHERE\n" +
                        "        dayid = '20220430'\n" +
                        "      GROUP BY\n" +
                        "        supply_id\n" +
                        "    ) a2\n" +
                        "),\n" +
                        "\n" +

                        "req_cnt_6m_cnt AS (\n" +
                        "  SELECT\n" +
                        "    a3.supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN a3.sum_req_cnt_6m <= 50 THEN 13\n" +
                        "        ELSE ''\n" +
                        "      end\n" +
                        "    ) AS supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        supply_id,\n" +
                        "        sum(req_cnt_6m) AS sum_req_cnt_6m\n" +
                        "      FROM\n" +
                        "        dws_hpc_flw_supply_device_login_d\n" +
                        "      WHERE\n" +
                        "        dayid = '20220430'\n" +
                        "      GROUP BY\n" +
                        "        supply_id\n" +
                        "    ) a3\n" +
                        "),\n" +
                        "\n" +

                        "req_cnt_1y_cnt AS (\n" +
                        "  SELECT\n" +
                        "    a4.supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN a4.sum_req_cnt_1y <= 100 THEN 14\n" +
                        "        ELSE ''\n" +
                        "      end\n" +
                        "    ) AS supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        supply_id,\n" +
                        "        sum(req_cnt_1y) AS sum_req_cnt_1y\n" +
                        "      FROM\n" +
                        "        dws_hpc_flw_supply_device_login_d\n" +
                        "      WHERE\n" +
                        "        dayid = '20220430'\n" +
                        "      GROUP BY\n" +
                        "        supply_id\n" +
                        "    ) a4\n" +
                        "),\n" +
                        "\n" +

                        "pay_total_amt_ratio_1y_total AS (\n" +
                        "  SELECT\n" +
                        "    a7.supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN a7.ratio <= 0.1 THEN 15\n" +
                        "        ELSE ''\n" +
                        "      end\n" +
                        "    ) AS supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        a5.supply_id,\n" +
                        "        (a5.pay_total_amt_tot1 / a6.pay_total_amt_tot2) AS ratio\n" +
                        "      FROM\n" +
                        "        (\n" +
                        "          SELECT\n" +
                        "            supply_id,\n" +
                        "            sum(pay_total_amt) AS pay_total_amt_tot1\n" +
                        "          FROM\n" +
                        "            dw_hpc_trd_com_ord_d\n" +
                        "          WHERE\n" +
                        "            dayid = '20220430'\n" +
                        "            AND datediff(\n" +
                        "              from_unixtime(\n" +
                        "                unix_timestamp('20220430', 'yyyyMMdd'),\n" +
                        "                'yyyy-MM-dd'\n" +
                        "              ),\n" +
                        "              from_unixtime(\n" +
                        "                unix_timestamp(substr(pay_time, 1, 8), 'yyyyMMdd'),\n" +
                        "                'yyyy-MM-dd'\n" +
                        "              )\n" +
                        "            ) <= 365\n" +
                        "            AND supply_id is NOT null\n" +
                        "          GROUP BY\n" +
                        "            supply_id\n" +
                        "        ) a5\n" +
                        "        LEFT JOIN (\n" +
                        "          SELECT\n" +
                        "            supply_id,\n" +
                        "            sum(pay_total_amt) AS pay_total_amt_tot2\n" +
                        "          FROM\n" +
                        "            dw_hpc_trd_com_ord_d\n" +
                        "          WHERE\n" +
                        "            dayid = '20220430'\n" +
                        "            AND supply_id is NOT null\n" +
                        "          GROUP BY\n" +
                        "            supply_id\n" +
                        "        ) a6 ON a5.supply_id = a6.supply_id\n" +
                        "        AND a6.supply_id is NOT null\n" +
                        "        AND a5.supply_id is NOT NULL\n" +
                        "    ) a7\n" +
                        "),\n" +
                        "\n" +

                        "pay_trd_cnt_ratio_1y_total AS (\n" +
                        "  SELECT\n" +
                        "    a10.supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN a10.ratio <= 0.1 THEN 16\n" +
                        "        ELSE ''\n" +
                        "      end\n" +
                        "    ) AS supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        a8.supply_id,\n" +
                        "        (a8.order_id_tot1 / a9.order_id_tot2) AS ratio\n" +
                        "      FROM\n" +
                        "        (\n" +
                        "          SELECT\n" +
                        "            supply_id,\n" +
                        "            count(order_id) AS order_id_tot1\n" +
                        "          FROM\n" +
                        "            dw_hpc_trd_com_ord_d\n" +
                        "          WHERE\n" +
                        "            dayid = '20220430'\n" +
                        "            AND datediff(\n" +
                        "              from_unixtime(unix_timestamp(dayid, 'yyyyMMdd'), 'yyyy-MM-dd'),\n" +
                        "              from_unixtime(\n" +
                        "                unix_timestamp(substr(pay_time, 1, 8), 'yyyyMMdd'),\n" +
                        "                'yyyy-MM-dd'\n" +
                        "              )\n" +
                        "            ) <= 365\n" +
                        "            AND supply_id is NOT null\n" +
                        "          GROUP BY\n" +
                        "            supply_id\n" +
                        "        ) a8\n" +
                        "        LEFT JOIN (\n" +
                        "          SELECT\n" +
                        "            supply_id,\n" +
                        "            count(order_id) AS order_id_tot2\n" +
                        "          FROM\n" +
                        "            dw_hpc_trd_com_ord_d\n" +
                        "          WHERE\n" +
                        "            dayid = '20220430'\n" +
                        "            AND supply_id is NOT null\n" +
                        "          GROUP BY\n" +
                        "            supply_id\n" +
                        "        ) a9 ON a8.supply_id = a9.supply_id\n" +
                        "        AND a9.supply_id is NOT null\n" +
                        "        AND a8.supply_id is NOT NULL\n" +
                        "    ) a10\n" +
                        "),\n" +
                        "\n" +

                        "sup_frez_cnt_1y_last_order_days_cnt AS (\n" +
                        "  SELECT\n" +
                        "    b5.supply_id,\n" +
                        "    (\n" +
                        "      case\n" +
                        "        WHEN (\n" +
                        "          b5.frez_cnt <= 1\n" +
                        "          AND b5.max_pay_time >= 90\n" +
                        "        ) THEN 17\n" +
                        "        ELSE ''\n" +
                        "      end\n" +
                        "    ) AS supply_del_rule_id\n" +
                        "  FROM\n" +
                        "    (\n" +
                        "      SELECT\n" +
                        "        b3.supply_id,\n" +
                        "        b3.frez_cnt,\n" +
                        "        b4.max_pay_time\n" +
                        "      FROM\n" +
                        "        (\n" +
                        "          SELECT\n" +
                        "            supply_id,\n" +
                        "            count(1) AS frez_cnt\n" +
                        "          FROM\n" +
                        "            (\n" +
                        "              SELECT\n" +
                        "                supplier_id AS supply_id,\n" +
                        "                opt_type\n" +
                        "              FROM\n" +
                        "                dwd_supplier_operation_d\n" +
                        "              WHERE\n" +
                        "                dayid = '20220430'\n" +
                        "                AND datediff(\n" +
                        "                  from_unixtime(unix_timestamp(dayid, 'yyyyMMdd'), 'yyyy-MM-dd'),\n" +
                        "                  from_unixtime(\n" +
                        "                    unix_timestamp(substr(opt_time, 1, 8), 'yyyyMMdd'),\n" +
                        "                    'yyyy-MM-dd'\n" +
                        "                  )\n" +
                        "                ) <= 365\n" +
                        "                AND opt_type = 13\n" +
                        "            ) b2\n" +
                        "          GROUP BY\n" +
                        "            supply_id\n" +
                        "        ) b3\n" +
                        "        LEFT JOIN (\n" +
                        "          SELECT\n" +
                        "            supply_id,\n" +
                        "            datediff(\n" +
                        "              current_date,\n" +
                        "              from_unixtime(\n" +
                        "                unix_timestamp(\n" +
                        "                  substr(max(pay_time), 1, 8),\n" +
                        "                  'yyyyMMdd'\n" +
                        "                ),\n" +
                        "                'yyyy-MM-dd'\n" +
                        "              )\n" +
                        "            ) AS max_pay_time\n" +
                        "          FROM\n" +
                        "            dw_hpc_trd_com_ord_d\n" +
                        "          WHERE\n" +
                        "            dayid = '20220430'\n" +
                        "          GROUP BY\n" +
                        "            supply_id\n" +
                        "        ) b4 ON b3.supply_id = b4.supply_id\n" +
                        "      WHERE\n" +
                        "        b3.supply_id is NOT null\n" +
                        "        AND b4.supply_id is NOT NULL\n" +
                        "    ) b5\n" +
                        ")\n" +
                        "\n" +

                        "insert overwrite table ads_hpc_sup_supply_compare_del_rules_m partition (dayid = '20220430')\n" +
                        "SELECT\n" +
                        "  d1.supply_id,\n" +
                        "  d2.supply_name,\n" +
                        "  d2.supply_prov_id,\n" +
                        "  d2.supply_prov_name,\n" +
                        "  d2.supply_city_id,\n" +
                        "  d2.supply_city_name,\n" +
                        "  d2.supply_area_id,\n" +
                        "  d2.supply_area_name,\n" +
                        "  d2.supply_is_inuse,\n" +
                        "  d2.supply_status,\n" +
                        "  d2.supply_status_name,\n" +
                        "  d2.supply_company_license_type,\n" +
                        "  d2.supply_company_license_type_name,\n" +
                        "  d2.supply_trade_type,\n" +
                        "  d2.supply_trade_type_name,\n" +
                        "  d2.supply_is_b_flag,\n" +
                        "  d2.supply_tags,\n" +
                        "  d2.supply_create_time,\n" +
                        "  d2.is_valid,\n" +
                        "  d1.supply_del_rule_id,\n" +
                        "  d3.supply_del_rule_name\n" +
                        "FROM\n" +
                        "  (\n" +
                        "    SELECT\n" +
                        "      *\n" +
                        "    FROM\n" +
                        "      supply_status_is_StopCope\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 1\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      pay_total_amt_6m\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 2\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      pay_total_amt_1y\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 3\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      pay_total_amt_ratio_1y_2y\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 4\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      pay_trd_cnt_6m\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 5\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      pay_trd_cnt_1y_sum_pay_total_amt_1y\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 6\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      last_order_days_cnt\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 7\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      sup_frez_cnt_ratio_1y\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 8\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      trace_type_cnt_3m\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 9\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      sup_service_behind_trace_type_cnt_3m\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 10\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      sup_shop_url_trace_type_cnt_3m\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 11\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      req_cnt_90d_cnt\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 12\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      req_cnt_6m_cnt\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 13\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      req_cnt_1y_cnt\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 14\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      pay_total_amt_ratio_1y_total\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 15\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      pay_trd_cnt_ratio_1y_total\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 16\n" +
                        "    UNION\n" +
                        "      all SELECT *\n" +
                        "    FROM\n" +
                        "      sup_frez_cnt_1y_last_order_days_cnt\n" +
                        "    WHERE\n" +
                        "      supply_del_rule_id = 17\n" +
                        "  ) d1\n" +
                        "  LEFT JOIN (\n" +
                        "    SELECT\n" +
                        "      supply_id,\n" +
                        "      supply_name,\n" +
                        "      supply_prov_id,\n" +
                        "      supply_prov_name,\n" +
                        "      supply_city_id,\n" +
                        "      supply_city_name,\n" +
                        "      supply_area_id,\n" +
                        "      supply_area_name,\n" +
                        "      supply_is_inuse,\n" +
                        "      supply_status,\n" +
                        "      supply_status_name,\n" +
                        "      supply_company_license_type,\n" +
                        "      supply_company_license_type_name,\n" +
                        "      supply_trade_type,\n" +
                        "      supply_trade_type_name,\n" +
                        "      supply_is_b_flag,\n" +
                        "      supply_tags,\n" +
                        "      supply_create_time,\n" +
                        "      is_valid\n" +
                        "    FROM\n" +
                        "      dim_ytj_sup_supply_d\n" +
                        "    WHERE\n" +
                        "      dayid = '20220430'\n" +
                        "  ) d2 ON d1.supply_id = d2.supply_id\n" +
                        "  LEFT JOIN dim_hpc_sup_supply_del_rules_m d3 ON d1.supply_del_rule_id = d3.supply_del_rule_id\n" +
                        "  AND d3.dayid = '20220430'";


        Tuple2<List<String>, String> sourceTableTargetTable = SqlAnalyze.extractTableFromWithQuery(sql);
        System.out.println(sourceTableTargetTable);
    }

    @Test
    public void test8() {
        //@formatter:off
        String sql =
                "with item_base as\n" +
                        "(\n" +
                        "select * from ytdw.dim_hpc_itm_item_d where dayid='20220929' and is_platform_valid = 1 and business_unit not in ('卡券票','其他')\n" +
                        "),\n" +
                        "is_shop_coupon as\n" +
                        "(\n" +
                        "--店铺券\n" +
                        "select\n" +
                        "item_id,\n" +
                        "1 as is_shop_coupon\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "store_id\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "supply_id\n" +
                        "from ytdw.dwd_item_extra_d\n" +
                        "lateral view explode(split(supply_ids, ',')) t as supply_id\n" +
                        "where dayid = '20220929' and is_deleted = 0\n" +
                        ") item\n" +
                        "left join\n" +
                        "(\n" +
                        "select\n" +
                        "id as store_id,\n" +
                        "supplier_id\n" +
                        "from ytdw.dwd_supplier_store_d\n" +
                        "where dayid = '20220929'\n" +
                        "and is_deleted = 0\n" +
                        ") supplier on item.supply_id = supplier.supplier_id\n" +
                        "where supplier.store_id is not null\n" +
                        ") item_store\n" +
                        "left join\n" +
                        "(\n" +
                        "select\n" +
                        "id as coupon_id,\n" +
                        "scope_type_value,\n" +
                        "initiator_value -- 发起方id\n" +
                        "from\n" +
                        "(\n" +
                        "select id,\n" +
                        "initiator_value\n" +
                        "from ytdw.dwd_coupon_d\n" +
                        "where dayid = '20220929'\n" +
                        "and is_deleted = 0\n" +
                        "and initiator_type = 1 -- 发放类型：小二发放 用户领取 任务发放\n" +
                        "and status = 1\n" +
                        "and (stop_date>=regexp_replace(current_date(),'-','') or stop_date is null)\n" +
                        ") coupon\n" +
                        "left join\n" +
                        "(\n" +
                        "select out_biz_id,\n" +
                        "scope_type_value2 as scope_type_value\n" +
                        "from ytdw.dwd_smc_promotion_scope_d\n" +
                        "lateral view explode(split(scope_type_value, ',')) t as scope_type_value2\n" +
                        "where dayid = '20220929'\n" +
                        "and is_deleted = 0\n" +
                        "and scope_type = 1 --1.包含前台类目2.包含后台类目3.包含品牌4.包含商品id5.不包含商品id6.商品包含大类7.商品包含标识\n" +
                        ") scope on coupon.id = scope.out_biz_id\n" +
                        ") coupon on item_store.store_id = coupon.initiator_value\n" +
                        "where coupon.initiator_value is not null\n" +
                        "and (\n" +
                        "coupon.scope_type_value = 'all'\n" +
                        "or coupon.scope_type_value = item_store.item_id\n" +
                        ")\n" +
                        "group by item_id\n" +
                        "),\n" +
                        "is_activity as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "count(distinct act_style_code) as activity_cnt,\n" +
                        "sum(case when act_name LIKE '%拿样%' then 1 else 0 end) as is_ny\n" +
                        "from ytdw.dw_hpc_prm_activity_item_d where dayid='20220929' and act_end_time>=regexp_replace(current_date(),'-','')\n" +
                        "group by item_id\n" +
                        "),\n" +
                        "is_one_piece as\n" +
                        "(\n" +
                        "select id as item_id,case when start_purchase_num=1 then 1 else 0 end as is_one_piece,\n" +
                        "sale_type,\n" +
                        "nvl(size(split(picture,';')),0) as pic_cnt,\n" +
                        "nvl(if(video_id is null,0,1),0) as is_video\n" +
                        "from ytdw.dwd_item_d where dayid='20220929'\n" +
                        "),\n" +
                        "tit_is_contain_brand as (\n" +
                        "select\n" +
                        "item.item_id,\n" +
                        "case when item.item_name like concat('%',brand.cn_name,'%') or item.item_name like concat('%',brand.en_name,'%') then 1 else 0 end as tit_is_brand,\n" +
                        "case when item.item_name like '%品牌直供%' then 1\n" +
                        "when item.item_name like '%厂家直销%' then 2\n" +
                        "when item.item_name like '%每日必抢%' then 3\n" +
                        "when item.item_name like '%巨划算%' then 4\n" +
                        "when item.item_name like '%秒杀%' then 5\n" +
                        "else 0 end as tit_is_spe\n" +
                        "from\n" +
                        "item_base item\n" +
                        "join\n" +
                        "ytdw.dwd_brand_d brand on brand.id=item.brand_id where brand.dayid='20220929'\n" +
                        "),\n" +
                        "tit_is_contain_cate as (\n" +
                        "select\n" +
                        "item_id,sum(is_contain_cate) as is_contain_cate\n" +
                        "from\n" +
                        "(\n" +
                        "select item_id,case when item_name like concat('%',real_category_name,'%') then 1 else 0 end as is_contain_cate\n" +
                        "from item_base lateral view explode(split(category_leaf_name,'\\/')) t as real_category_name\n" +
                        ") t\n" +
                        "group by item_id\n" +
                        "),\n" +
                        "tit_is_contain_pro as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "item_name,\n" +
                        "sum(is_contain_pro_rep) as is_contain_pro_rep\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,item_name,\n" +
                        "case when item_name like concat('%',property_rep,'%') then 1 else 0 end as is_contain_pro_rep\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item.item_id,item_name,property_set,required_property_set\n" +
                        "from\n" +
                        "(select item_id,item_name from item_base) item\n" +
                        "left join\n" +
                        "(\n" +
                        "select item_property.item_id,\n" +
                        "collect_set(regexp_replace(property_value,'\"','')) as property_set,\n" +
                        "collect_set(case when category_property.is_required=1 then regexp_replace(property_value,'\"','') else null end) as required_property_set\n" +
                        "from\n" +
                        "(\n" +
                        "select * from ytdw.dwd_item_property_d where dayid='20220929' and is_deleted=0 and property_value is not null and property_value != ''\n" +
                        "--部分商品没有属性，如305097，是因为类目没有属性需要填\n" +
                        ") item_property\n" +
                        "left join\n" +
                        "(\n" +
                        "select * from ytdw.dwd_category_property_d where dayid='20220929' and is_deleted=0\n" +
                        "--部分类目没有属性，如8011，8006，8007\n" +
                        ") category_property on category_property.id=item_property.category_property_id\n" +
                        "group by item_property.item_id\n" +
                        ") pro on pro.item_id=item.item_id\n" +
                        ") t\n" +
                        "lateral view explode(required_property_set) t3 as property_rep\n" +
                        ") tmp\n" +
                        "group by item_id,item_name\n" +
                        "),\n" +
                        "is_material as (\n" +
                        "select biz_value as item_id,count(distinct material_id) as cnt_material --卖货素材数\n" +
                        "from ytdw.dwd_material_relations_d\n" +
                        "where dayid='20220929' and biz_type=2 and is_deleted = 0\n" +
                        "group by biz_value\n" +
                        "),\n" +
                        "sale_service as (\n" +
                        "select\n" +
                        "item_id,\n" +
                        "count(0) as cnt\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item.item_id,\n" +
                        "item.item_name,\n" +
                        "item.after_sale_tag,\n" +
                        "after_sale_tag.tag_name as after_sale_tag_name\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "id as item_id,\n" +
                        "item_name,\n" +
                        "after_sale_tag\n" +
                        "from ytdw.ods_t_item_d\n" +
                        "lateral view explode(split(after_sale_tags,',')) after_sale_tags as after_sale_tag\n" +
                        "where dayid = '20220929'\n" +
                        "--and bu_id = 0\n" +
                        "--and after_sale_tags is not null\n" +
                        "--and length(after_sale_tag) > 0\n" +
                        ") as item\n" +
                        "left join\n" +
                        "(\n" +
                        "select\n" +
                        "id,\n" +
                        "tag_name\n" +
                        "from ytdw.dwd_item_after_sale_tag_d\n" +
                        "where dayid = '20220929'\n" +
                        "and is_deleted = 0\n" +
                        ") as after_sale_tag\n" +
                        "on item.after_sale_tag = after_sale_tag.id\n" +
                        "--where item.item_name like '%惠氏启赋4段%'\n" +
                        ") t group by item_id\n" +
                        "),\n" +
                        "logistic_service as (\n" +
                        "select\n" +
                        "item_id,\n" +
                        "count(0) as cnt,\n" +
                        "concat_ws(',',collect_set(logistic_type_name)) as logistic_type_names\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item.item_id,\n" +
                        "item.item_name,\n" +
                        "item.logistic_template_id,\n" +
                        "logistic_carry.logistic_type,\n" +
                        "case when logistic_carry.logistic_type = 1 then '快递'\n" +
                        "when logistic_carry.logistic_type = 2 then '物流到店'\n" +
                        "when logistic_carry.logistic_type = 3 then '物流自提'\n" +
                        "when logistic_carry.logistic_type = 4 then '供应商配送'\n" +
                        "when logistic_carry.logistic_type = 5 then '落地配'\n" +
                        "when logistic_carry.logistic_type = 6 then '品骏快递'\n" +
                        "when logistic_carry.logistic_type = 7 then '京东配送'\n" +
                        "when logistic_carry.logistic_type = 8 then '顺丰速递'\n" +
                        "when logistic_carry.logistic_type = 9 then '极兔速递'\n" +
                        "else null end as logistic_type_name\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "id as item_id,\n" +
                        "item_name,\n" +
                        "logistic_template_id\n" +
                        "from ytdw.dwd_item_full_d\n" +
                        "where dayid = '20220929'\n" +
                        "and logistic_template_id is not null\n" +
                        ") as item\n" +
                        "left join\n" +
                        "(\n" +
                        "select\n" +
                        "logistic_template_id,\n" +
                        "logistic_type\n" +
                        "from ytdw.dwd_logistic_carry_d\n" +
                        "where dayid = '20220929'\n" +
                        "and is_deleted = 0\n" +
                        "group by logistic_template_id, logistic_type\n" +
                        ") as logistic_carry on logistic_carry.logistic_template_id = item.logistic_template_id\n" +
                        ") t group by item_id\n" +
                        "),\n" +
                        "stage1 as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "collect_set(property_value)[0] as milk_stage1,\n" +
                        "case\n" +
                        "when collect_set(property_value)[0] rlike '1段|一段|pre段' then '1段'\n" +
                        "when collect_set(property_value)[0] rlike '2段|二段' then '2段'\n" +
                        "when collect_set(property_value)[0] rlike '3段|三段|1\\\\+段|2\\\\+段' then '3段'\n" +
                        "when collect_set(property_value)[0] rlike '4段|四段|5段|五段|6段|六段|儿童' then '儿童'\n" +
                        "when collect_set(property_value)[0] rlike '成人' then '成人'\n" +
                        "else '其他' end as milk_stage2\n" +
                        "from ytdw.dwd_item_property_d\n" +
                        "where dayid='20220929'\n" +
                        "and property_name in ('适用段数','适用阶段')\n" +
                        "and is_deleted=0\n" +
                        "and property_value is not null\n" +
                        "group by item_id\n" +
                        "),\n" +
                        "weight1 as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "cast(collect_set(property_value)[0] as int) as weight\n" +
                        "from ytdw.dwd_item_property_d\n" +
                        "where dayid='20220929'\n" +
                        "and property_name in ('净重')\n" +
                        "and is_deleted=0\n" +
                        "and property_value is not null\n" +
                        "group by item_id\n" +
                        "),\n" +
                        "stage2 as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "item_name,\n" +
                        "case\n" +
                        "when lower(item_name) rlike '1段|一段|pre段' then '1段'\n" +
                        "when item_name rlike '2段|二段' then '2段'\n" +
                        "when item_name rlike '3段|三段|1\\\\+段|2\\\\+段' then '3段'\n" +
                        "when item_name rlike '4段|四段|5段|五段|6段|六段|儿童' then '儿童'\n" +
                        "when item_name rlike '成人' then '成人'\n" +
                        "else '其他'\n" +
                        "end as milk_stage2,\n" +
                        "case when item_name like '%新包装%' then '新包装' else null end as pack,\n" +
                        "case when item_name like '%瘪罐%' or item_name like '%破盖%' then '瘪罐/破盖' else null end as damage,\n" +
                        "regexp_extract(item_name,'（(.{1,})）[^）]',1) as brackets1,\n" +
                        "regexp_extract(item_name,'【(.*)】[^】]',1) as brackets2,\n" +
                        "cast(case when regexp_extract(item_name,'(\\\\d{1,})[g|克|G]',1)!='' then\n" +
                        "regexp_extract(item_name,'(\\\\d{1,})[g|克|G]',1)\n" +
                        "else\n" +
                        "cast(regexp_extract(item_name,'(\\\\d{1,})[kg|千克|KG]',1) as int)*1000 end as int) as gram\n" +
                        "from item_base\n" +
                        "),\n" +
                        "loca as\n" +
                        "(\n" +
                        "select\n" +
                        "t1.item_id,\n" +
                        "concat_ws(',',collect_set(locality)) as locality,\n" +
                        "concat_ws(',',collect_set(t2.series_name)) as series_name\n" +
                        "from\n" +
                        "(\n" +
                        "select * from ytdw.dw_item_d WHERE dayid ='20220929' --and category_id_first_name='奶粉'\n" +
                        ") t1\n" +
                        "left join\n" +
                        "(\n" +
                        "select\n" +
                        "id,\n" +
                        "brand_id,\n" +
                        "series_name\n" +
                        "from ytdw.dwd_brand_series_d where dayid='20220929'\n" +
                        ") t2 on t2.id=t1.brand_series_id\n" +
                        "group by t1.item_id\n" +
                        "),\n" +
                        "item_pool as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "cast(collect_set(item_pool_id)[0] as int) as item_pool_id\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "id as item_pool_id,\n" +
                        "item_id,\n" +
                        "pool_name as item_pool_name,\n" +
                        "pool_type\n" +
                        "from ytdw.dwd_item_pool_d\n" +
                        "lateral view explode(split(item_ids,',')) tmp as item_id\n" +
                        "where dayid='20220929' and is_deleted=0\n" +
                        ") t1 group by item_id\n" +
                        "),\n" +
                        "pay_price as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "pay_time,\n" +
                        "pay_price,\n" +
                        "avg_pay_price_7ds,\n" +
                        "avg_pay_price_15ds,\n" +
                        "avg_pay_price_30ds,\n" +
                        "avg_pay_price_90ds\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "item_actual_amount/item_count as pay_price,\n" +
                        "pay_time,\n" +
                        "row_number() over(partition by item_id order by pay_time desc,item_actual_amount/item_count) as rn,\n" +
                        "avg(case when pay_day>='20220922' then item_actual_amount/item_count else null end) over(partition by item_id) as avg_pay_price_7ds,\n" +
                        "avg(case when pay_day>='20220914' then item_actual_amount/item_count else null end) over(partition by item_id) as avg_pay_price_15ds,\n" +
                        "avg(case when pay_day>='20220830' then item_actual_amount/item_count else null end) over(partition by item_id) as avg_pay_price_30ds,\n" +
                        "avg(case when pay_day>='20220701' then item_actual_amount/item_count else null end) over(partition by item_id) as avg_pay_price_90ds\n" +
                        "from ytdw.dw_order_d\n" +
                        "where dayid='20220929' and pay_day>='20220701'\n" +
                        "and pay_time is not null\n" +
                        "--and category_1st_name in ('奶粉') and business_unit in ('大贸奶纸')\n" +
                        ") t1 where rn=1\n" +
                        "),\n" +
                        "mai_tbl as\n" +
                        "(\n" +
                        "select\n" +
                        "*\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "cast(split(code_extend_fields['price'],'-')[0] as decimal(18,2)) as price,\n" +
                        "code_extend_fields['active_info_tag'] as active_info_tag,\n" +
                        "cast(regexp_extract(code_extend_fields['return_rate'],'(\\\\d{1,}\\\\.\\\\d{1,})',1) as decimal(18,2)) as return_rate,\n" +
                        "code_extend_fields['item_tags_info'] as item_tags_info,\n" +
                        "code_extend_fields['top_name'] as top_name,\n" +
                        "request_time,\n" +
                        "row_number() over(partition by item_id order by request_time desc) as rn\n" +
                        "from ytdw.dw_hpc_flw_search_area_exposure_di where dayid>=''\n" +
                        ") t where rn=1\n" +
                        "),\n" +
                        "is_store as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "collect_set(store_id)[0] as store_id\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "supply_id\n" +
                        "from ytdw.dwd_item_extra_d\n" +
                        "lateral view explode(split(supply_ids, ',')) t as supply_id\n" +
                        "where dayid = '20220929' and is_deleted = 0\n" +
                        ") item\n" +
                        "left join\n" +
                        "(\n" +
                        "select\n" +
                        "id as store_id,\n" +
                        "supplier_id\n" +
                        "from ytdw.dwd_supplier_store_d\n" +
                        "where dayid = '20220929'\n" +
                        "and is_deleted = 0 and store_type=2\n" +
                        ") supplier on item.supply_id = supplier.supplier_id\n" +
                        "where supplier.store_id is not null\n" +
                        "group by item_id\n" +
                        "),\n" +
                        "trade_shop as\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "floor(pow(trade_count,0.9)*10) as trade_count\n" +
                        "from\n" +
                        "(\n" +
                        "select\n" +
                        "item_id,\n" +
                        "sum(popularity) as popularity,\n" +
                        "sum(sales_volume) as sales_volume,\n" +
                        "sum(trade_count) as trade_count\n" +
                        "from reco.st_item_popularity_d\n" +
                        "where dayid='20220929'\n" +
                        "group by item_id\n" +
                        ") t1\n" +
                        ")\n" +
                        "insert overwrite table reco.dws_item_base_info_d partition (dayid='20220929')\n" +
                        "select\n" +
                        "base.item_id,\n" +
                        "base.brand_id,\n" +
                        "base.category_1st_id,\n" +
                        "base.category_2nd_id,\n" +
                        "base.category_3rd_id,\n" +
                        "base.category_leaf_id,\n" +
                        "ticb.tit_is_brand as tit_is_contain_brand,\n" +
                        "ticc.is_contain_cate as tit_is_contain_cate,\n" +
                        "case when ticp.is_contain_pro_rep>0 then 1 else 0 end as tit_is_contain_pro,\n" +
                        "nvl(ticp.is_contain_pro_rep,0) as tit_is_contain_pro_cnt,\n" +
                        "ticb.tit_is_spe as tit_type,\n" +
                        "case when i_act.is_ny>0 then 1 else 0 end as is_ny,\n" +
                        "nvl(i_one_p.is_one_piece,0) as is_one_piece,\n" +
                        "i_one_p.is_video as is_video,\n" +
                        "i_one_p.pic_cnt as pic_cnt,\n" +
                        "case when im.cnt_material>0 then 1 else 0 end as is_material,\n" +
                        "case when i_act.item_id is not null then 1 else 0 end as is_activity,\n" +
                        "nvl(i_act.activity_cnt,0) as activity_cnt,\n" +
                        "case when ss.cnt>0 then 1 else 0 end as is_service,\n" +
                        "nvl(ss.cnt,0) as service_cnt,\n" +
                        "null as manufacture_date,\n" +
                        "null as manufacture_date_type,\n" +
                        "null as spec,\n" +
                        "null as item_stock,\n" +
                        "null as sku_cnt,\n" +
                        "ls.logistic_type_names as logistics_type,\n" +
                        "null as logistics_price,\n" +
                        "i_one_p.sale_type,\n" +
                        "case when i_sc.item_id is not null then 1 else 0 end as is_shop_coupon,\n" +
                        "base.item_name,\n" +
                        "s1.milk_stage1 as milk_stage_property,\n" +
                        "s2.milk_stage2 as milk_stage_title,\n" +
                        "cast(s2.gram as int),\n" +
                        "case when s2.pack is not null then 1 else 0 end as is_new_pack,\n" +
                        "case when s2.damage is not null then 1 else 0 end as is_damage,\n" +
                        "lc.series_name as series_name,\n" +
                        "s2.brackets1 as small_brackets_info,\n" +
                        "s2.brackets2 as middle_brackets_info,\n" +
                        "ip.item_pool_id as item_pool_id,\n" +
                        "pp.pay_price as max_pay_price,\n" +
                        "mt.price as max_exp_price,\n" +
                        "mt.active_info_tag as active_info_tag,\n" +
                        "ts.trade_count,\n" +
                        "mt.return_rate,\n" +
                        "case when iss.store_id is not null then 1 else 0 end as is_store,\n" +
                        "mt.top_name,\n" +
                        "mt.item_tags_info,\n" +
                        "case when s1.milk_stage2=s2.milk_stage2 then s2.milk_stage2 else '异常' end as valid_stage,\n" +
                        "w1.weight as property_gram,\n" +
                        "case when s2.gram=w1.weight then s2.gram else null end as valid_gram,\n" +
                        "pp.pay_time as max_pay_time,\n" +
                        "pp.avg_pay_price_7ds as avg_pay_price_7ds,\n" +
                        "pp.avg_pay_price_15ds as avg_pay_price_15ds,\n" +
                        "pp.avg_pay_price_30ds as avg_pay_price_30ds,\n" +
                        "pp.avg_pay_price_90ds as avg_pay_price_90ds\n" +
                        "from\n" +
                        "item_base base\n" +
                        "left join\n" +
                        "is_shop_coupon i_sc on i_sc.item_id=base.item_id\n" +
                        "left join\n" +
                        "is_activity i_act on i_act.item_id=base.item_id\n" +
                        "left join\n" +
                        "is_one_piece i_one_p on i_one_p.item_id=base.item_id\n" +
                        "left join\n" +
                        "tit_is_contain_brand ticb on ticb.item_id=base.item_id\n" +
                        "left join\n" +
                        "tit_is_contain_cate ticc on ticc.item_id=base.item_id\n" +
                        "left join\n" +
                        "tit_is_contain_pro ticp on ticp.item_id=base.item_id\n" +
                        "left join\n" +
                        "is_material im on im.item_id=base.item_id\n" +
                        "left join\n" +
                        "sale_service ss on ss.item_id=base.item_id\n" +
                        "left join\n" +
                        "logistic_service ls on ls.item_id=base.item_id\n" +
                        "left join\n" +
                        "stage1 s1 on s1.item_id=base.item_id\n" +
                        "left join\n" +
                        "stage2 s2 on s2.item_id=base.item_id\n" +
                        "left join\n" +
                        "loca lc on lc.item_id=base.item_id\n" +
                        "left join\n" +
                        "item_pool ip on ip.item_id=base.item_id\n" +
                        "left join\n" +
                        "pay_price pp on pp.item_id=base.item_id\n" +
                        "left join\n" +
                        "mai_tbl mt on mt.item_id=base.item_id\n" +
                        "left join\n" +
                        "is_store iss on iss.item_id=base.item_id\n" +
                        "left join\n" +
                        "is_shop_coupon isc on isc.item_id=base.item_id\n" +
                        "left join\n" +
                        "trade_shop ts on ts.item_id=base.item_id\n" +
                        "left join\n" +
                        "weight1 w1 on w1.item_id=base.item_id";
        //@formatter:on

        Tuple2<List<String>, String> sourceTableTargetTable = SqlAnalyze.extractTableFromWithQuery(sql);
        System.out.println(sourceTableTargetTable);
    }


    @Test
    public void test9() {
        //@formatter:off
        String sql = "" +
                "with tmp1 as ( select key from srcTable where key = '5'),\n" +
                "tmp2 as ( select key from tmp1 union select * from tmp3 where id in (select id from table1)  and key = '5')\n" +
                "select *\n" +
                "from tmp1\n" +
                "where id in (select id from tmp11 )" +
                ";\n";
        //@formatter:on

        Tuple2<List<String>, String> sourceTableTargetTable = SqlAnalyze.extractTableFromWithQuery(sql);
        System.out.println(sourceTableTargetTable);




    }

}
