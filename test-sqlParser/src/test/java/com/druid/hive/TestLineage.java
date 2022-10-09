package com.druid.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveCreateTableParser;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.druid.common.lineage.pojo.HiveLineageVisitor;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author hubin
 * @date 2022年09月21日 17:03
 */
public class TestLineage {
    @Test
    public void test1() {
        String sql = "SELECT * FROM db1.table_test_1;";
        HiveStatementParser parser = new HiveStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();

        HiveLineageVisitor hiveLineageVisitor = new HiveLineageVisitor();
        sqlStatement.accept(hiveLineageVisitor);

        System.out.println(hiveLineageVisitor.getSource());


    }

    @Test
    public void test2() {
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
        // @formatter:on

        String withSelectSql = StringUtils.replace(sql, " all.", " a.");

        List<SQLStatement> statementList = SQLUtils.parseStatements(withSelectSql, JdbcConstants.HIVE);
        SQLStatement sqlStatement = statementList.get(0);



        HiveLineageVisitor hiveLineageVisitor = new HiveLineageVisitor();
        sqlStatement.accept(hiveLineageVisitor);

        System.out.println(hiveLineageVisitor.getSource());
        System.out.println(hiveLineageVisitor.getTarget());
        System.out.println(hiveLineageVisitor.getTemporaries());

    }

    @Test
    public void test4() {
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

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        SQLStatement sqlStatement = statementList.get(0);



        HiveLineageVisitor hiveLineageVisitor = new HiveLineageVisitor();
        sqlStatement.accept(hiveLineageVisitor);

        System.out.println(hiveLineageVisitor.getSource());
        System.out.println(hiveLineageVisitor.getTarget());
        System.out.println(hiveLineageVisitor.getTemporaries());


    }


}
