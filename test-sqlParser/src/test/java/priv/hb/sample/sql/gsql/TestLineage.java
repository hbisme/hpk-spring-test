package priv.hb.sample.sql.gsql;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Set;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TCTEList;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TExpressionList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableList;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import io.vavr.Function0;
import io.vavr.collection.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author hubin
 * @date 2022年09月23日 15:41
 */
public class TestLineage {
    @Test
    public void test1() {
        String sql =
                "insert overwrite table ytdw.dw_flw_visit_merge_di partition (dayid = '20220908')\n" +
                        "select\n" +
                        "  id,\n" +
                        "  shop_id,\n" +
                        "  deviceno,\n" +
                        "  '1' as visit_style\n" +
                        "from\n" +
                        "  crm.srcTable\n" +
                        "where\n" +
                        "  dayid = '20220908'";


        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sql;
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement tCustomSqlStatement = (TInsertSqlStatement) (sqlparser.getSqlstatements().get(0));

        TTable targetTable = tCustomSqlStatement.getTargetTable();

        String tableName = targetTable.getTableName().getTableString();
        String dbName = targetTable.getTableName().getSchemaString();
        TExpressionList partition = targetTable.getPartitionExtensionClause().getKeyValues();

        TExpression partitionName = partition.getExpression(0).getLeftOperand();
        TExpression partitionValue = partition.getExpression(0).getRightOperand();


        tCustomSqlStatement.getSubQuery();


        System.out.println(tCustomSqlStatement);
    }

    @Test
    public void test2() {
        // String sql =
        //         "insert overwrite table ytdw.dw_flw_visit_merge_di partition (dayid = '20220908')\n" +
        //                 "select\n" +
        //                 "  id,\n" +
        //                 "  shop_id,\n" +
        //                 "  deviceno,\n" +
        //                 "  '1' as visit_style\n" +
        //                 "from\n" +
        //                 "  crm.srcTable\n" +
        //                 "where\n" +
        //                 "  dayid = '20220908'";

        // String sql =  "SELECT page_views.*\n" +
        //         "FROM page_views JOIN dim_users\n" +
        //         "  ON (page_views.user_id = dim_users.id " +
        //         "      AND page_views.date >= '2008-03-01' " +
        //         "      AND page_views.date <= '2008-03-31')";
        //
        // String sql = "with tmp1 as ( select key from srcTable where key = '5')\n" +
        //         "select *\n" +
        //         "from tmp1;\n";
        //
        //
        // String sql =
        //         "with \n" +
        //                 "  a as (select * from src1 where key is not null),\n" +
        //                 "  b as (select  * from src2 where value>0),\n" +
        //                 "  c as (select * from src3 where value>0),\n" +
        //                 "  d as (select a.key,b.value from a join b on a.key=b.key),\n" +
        //                 "  e as (select a.key,c.value from a left outer join c on a.key=c.key and c.key is not null)\n" +
        //                 "insert overwrite table targetTable partition (p='abc')\n" +
        //                 "select * from d union all select * from targetTable union all select * from sourceTable1;\n";

        String sql2 =
                "create table if not exists st_abtest_search_long_tail_report_d("
                        + "     exp_code string comment '实验编号',"
                        + "     bucket_type int comment '分桶类型') comment '搜索大长尾AB测试报表'"
                        + "   partitioned by (dayid string) stored as orc";


        // String sql =
        //         "create view if not exists rtdw.ods_vf_crm_visit_history as " +
        //                 "select t2.* " +
        //                 "from (select *, " +
        //                 "             ROW_NUMBER( " +
        //                 "                 ) OVER (PARTITION BY id ORDER BY kafka_offset DESC " +
        //                 "                 ) rn " +
        //                 "      from (select * " +
        //                 "            from rtdw_mid.ods_crm_visit_history_offline " +
        //                 "            union all " +
        //                 "            select * " +
        //                 "            from rtdw_mid.ods_crm_visit_history_json " +
        //                 "           ) t1 " +
        //                 "     ) t2 " +
        //                 "where t2.rn = 1 " +
        //                 "  and t2.mysql_optype != 'DELETE';";

        // String sql = "with seq_num_tab as(\n" +
        //         "    select 1 as seqnum\n" +
        //         "    union all\n" +
        //         "    select sequnum + 1\n" +
        //         "    from source_num_tab\n" +
        //         "    where seqnum <100\n" +
        //         ")\n" +
        //         "select seqnum from seq_num_tab\n" +
        //         "\n";

        //@formatter:off
        String sql =
                "INSERT OVERWRITE TABLE table1.target_contract_gmv_d PARTITION (dayid='${v_date}')\n" +
                        "SELECT crm_contract_shop.contract_id,\n" +
                        "         crm_contract_shop.shop_id,\n" +
                        "         sum(if(shop_trd.date_id >= substr(crm_contract.start_time,\n" +
                        "         1,\n" +
                        "         8)\n" +
                        "        AND shop_trd.date_id <= substr(crm_contract.end_time, 1, 8), shop_trd.net_pay_amt_1d_a_style_frez, 0)) AS a_gmv, sum(if(shop_trd.date_id >= substr(crm_contract.start_time, 1, 8)\n" +
                        "        AND shop_trd.date_id <= substr(crm_contract.end_time, 1, 8), shop_trd.net_pay_amt_1d_b_style_frez, 0)) AS b_gmv\n" +
                        "FROM \n" +
                        "    (SELECT *\n" +
                        "    FROM ytdw.source_crm_contract_d\n" +
                        "    WHERE dayid = '${v_date}'\n" +
                        "    AND is_deleted = 0 ) crm_contract_shop";
        //@formatter:on


        // String sql =
        //         "insert overwrite directory '/tmp/tmp_sync_t_smc_coupon_owner_get_serial_b_id_temp' select null as id ,coupon_owner_get_serial_b_id from ytdw_temp.tmp_sync_t_smc_coupon_owner_get_serial_b_id ";

        // String sql =
        //         "with hive_info as (\n" +
        //                 "  select\n" +
        //                 "    max(dayid) as cur_dayid,\n" +
        //                 "    round(\n" +
        //                 "      sum(\n" +
        //                 "        case\n" +
        //                 "          when dayid <> '20220820' then a.tbl_size_G\n" +
        //                 "          else 0\n" +
        //                 "        end\n" +
        //                 "      ) / 1024,\n" +
        //                 "      2\n" +
        //                 "    ) as hive_size_pre,\n" +
        //                 "    round(\n" +
        //                 "      sum(\n" +
        //                 "        case\n" +
        //                 "          when dayid = '20220820' then a.tbl_size_G\n" +
        //                 "          else 0\n" +
        //                 "        end\n" +
        //                 "      ) / 1024,\n" +
        //                 "      2\n" +
        //                 "    ) as hive_size_cur,\n" +
        //                 "    round(\n" +
        //                 "      sum(\n" +
        //                 "        case\n" +
        //                 "          when dayid = '20220820'\n" +
        //                 "          and b.tbl_name is not null then a.tbl_size_G\n" +
        //                 "          else 0\n" +
        //                 "        end\n" +
        //                 "      ) / 1024,\n" +
        //                 "      2\n" +
        //                 "    ) as hive_inused_tb_size_cur,\n" +
        //                 "    round(\n" +
        //                 "      sum(\n" +
        //                 "        case\n" +
        //                 "          when dayid = '20220820'\n" +
        //                 "          and max_dayid >= '20220721' then a.tbl_size_G\n" +
        //                 "          else 0\n" +
        //                 "        end\n" +
        //                 "      ) / 1024,\n" +
        //                 "      2\n" +
        //                 "    ) as hive_inused_tb_size_last_30d,\n" +
        //                 "    round(\n" +
        //                 "      sum(\n" +
        //                 "        case\n" +
        //                 "          when dayid = '20220820'\n" +
        //                 "          and max_dayid >= '20220522' then a.tbl_size_G\n" +
        //                 "          else 0\n" +
        //                 "        end\n" +
        //                 "      ) / 1024,\n" +
        //                 "      2\n" +
        //                 "    ) as hive_inused_tb_size_last_90d,\n" +
        //                 "    round(\n" +
        //                 "      sum(\n" +
        //                 "        case\n" +
        //                 "          when dayid = '20220820'\n" +
        //                 "          and b.tbl_name is null then a.tbl_size_G\n" +
        //                 "          else 0\n" +
        //                 "        end\n" +
        //                 "      ) / 1024,\n" +
        //                 "      2\n" +
        //                 "    ) as hive_unused_tb_size_cur,\n" +
        //                 "    round(\n" +
        //                 "      sum(\n" +
        //                 "        case\n" +
        //                 "          when dayid <> '20220820'\n" +
        //                 "          or (\n" +
        //                 "            dayid = '20220820'\n" +
        //                 "            and max_dayid >= '20220721'\n" +
        //                 "          ) then 0\n" +
        //                 "          else a.tbl_size_G\n" +
        //                 "        end\n" +
        //                 "      ) / 1024,\n" +
        //                 "      2\n" +
        //                 "    ) as hive_unused_tb_size_last_30d,\n" +
        //                 "    round(\n" +
        //                 "      sum(\n" +
        //                 "        case\n" +
        //                 "          when dayid <> '20220820'\n" +
        //                 "          or (\n" +
        //                 "            dayid = '20220820'\n" +
        //                 "            and max_dayid >= '20220522'\n" +
        //                 "          ) then 0\n" +
        //                 "          else a.tbl_size_G\n" +
        //                 "        end\n" +
        //                 "      ) / 1024,\n" +
        //                 "      2\n" +
        //                 "    ) as hive_unused_tb_size_last_90d,\n" +
        //                 "    sum(\n" +
        //                 "      case\n" +
        //                 "        when dayid = '20220820' then 1\n" +
        //                 "        else 0\n" +
        //                 "      end\n" +
        //                 "    ) as hive_tb_cnt_cur,\n" +
        //                 "    sum(\n" +
        //                 "      case\n" +
        //                 "        when dayid = '20220820'\n" +
        //                 "        and b.tbl_name is not null then 1\n" +
        //                 "        else 0\n" +
        //                 "      end\n" +
        //                 "    ) as hive_inused_tb_cnt_cur,\n" +
        //                 "    sum(\n" +
        //                 "      case\n" +
        //                 "        when dayid = '20220820'\n" +
        //                 "        and max_dayid >= '20220721' then 1\n" +
        //                 "        else 0\n" +
        //                 "      end\n" +
        //                 "    ) as hive_inused_tb_cnt_last_30d,\n" +
        //                 "    sum(\n" +
        //                 "      case\n" +
        //                 "        when dayid = '20220820'\n" +
        //                 "        and max_dayid >= '20220522' then 1\n" +
        //                 "        else 0\n" +
        //                 "      end\n" +
        //                 "    ) as hive_inused_tb_cnt_last_90d,\n" +
        //                 "    sum(\n" +
        //                 "      case\n" +
        //                 "        when dayid = '20220820'\n" +
        //                 "        and b.tbl_name is null then 1\n" +
        //                 "        else 0\n" +
        //                 "      end\n" +
        //                 "    ) as hive_unused_tb_cnt_cur,\n" +
        //                 "    sum(\n" +
        //                 "      case\n" +
        //                 "        when dayid <> '20220820'\n" +
        //                 "        or (\n" +
        //                 "          dayid = '20220820'\n" +
        //                 "          and max_dayid >= '20220721'\n" +
        //                 "        ) then 0\n" +
        //                 "        else 1\n" +
        //                 "      end\n" +
        //                 "    ) as hive_unused_tb_cnt_last_30d,\n" +
        //                 "    sum(\n" +
        //                 "      case\n" +
        //                 "        when dayid <> '20220820'\n" +
        //                 "        or (\n" +
        //                 "          dayid = '20220820'\n" +
        //                 "          and max_dayid >= '20220522'\n" +
        //                 "        ) then 0\n" +
        //                 "        else 1\n" +
        //                 "      end\n" +
        //                 "    ) as hive_unused_tb_cnt_last_90d\n" +
        //                 "  from\n" +
        //                 "    (\n" +
        //                 "      select\n" +
        //                 "        db_name,\n" +
        //                 "        tbl_name,\n" +
        //                 "        tbl_size_G,\n" +
        //                 "        dayid\n" +
        //                 "      from\n" +
        //                 "        dws_ytj_met_tb_pt_storage_reduce_detail_di\n" +
        //                 "      where\n" +
        //                 "        dayid > '20220818'\n" +
        //                 "    ) a\n" +
        //                 "    left join (\n" +
        //                 "      select\n" +
        //                 "        db_name,\n" +
        //                 "        tbl_name,\n" +
        //                 "        max(dayid) as dayid_max,\n" +
        //                 "        count(distinct dayid) as dayid_cnt,\n" +
        //                 "        max(dayid) as max_dayid\n" +
        //                 "      from\n" +
        //                 "        dim_ytj_met_tb_life_cycle_detail_di\n" +
        //                 "      where\n" +
        //                 "        dayid > '0'\n" +
        //                 "      group by\n" +
        //                 "        db_name,\n" +
        //                 "        tbl_name\n" +
        //                 "    ) b on a.tbl_name = b.tbl_name\n" +
        //                 "    and a.db_name = b.db_name\n" +
        //                 "),\n" +
        //                 "\n" +
        //
        //                 "hdfs_info as (\n" +
        //                 "  select\n" +
        //                 "    round(\n" +
        //                 "      sum(\n" +
        //                 "        case\n" +
        //                 "          when dayid = '20220820' then replication * file_size\n" +
        //                 "          else 0\n" +
        //                 "        end\n" +
        //                 "      ) / 1024 / 1024 / 1024 / 1024,\n" +
        //                 "      2\n" +
        //                 "    ) as hdfs_size_cur,\n" +
        //                 "    round(\n" +
        //                 "      sum(\n" +
        //                 "        case\n" +
        //                 "          when dayid <> '20220820' then replication * file_size\n" +
        //                 "          else 0\n" +
        //                 "        end\n" +
        //                 "      ) / 1024 / 1024 / 1024 / 1024,\n" +
        //                 "      2\n" +
        //                 "    ) as hdfs_size_pre\n" +
        //                 "  from\n" +
        //                 "    dwd_hdfs_meta_d\n" +
        //                 "  where\n" +
        //                 "    dayid > '20220818'\n" +
        //                 "    and dayid <= '20220820'\n" +
        //                 ")\n" +
        //                 "\n" +
        //
        //                 "insert into ads_met_hdfs_hive_size_d\n" +
        //                 "SELECT\n" +
        //                 "  cur_dayid,\n" +
        //                 "  hdfs_size_cur,\n" +
        //                 "  hdfs_size_pre,\n" +
        //                 "  hive_size_cur,\n" +
        //                 "  hive_size_pre,\n" +
        //                 "  hive_inused_tb_size_cur,\n" +
        //                 "  hive_inused_tb_size_last_30d,\n" +
        //                 "  hive_inused_tb_size_last_90d,\n" +
        //                 "  hive_unused_tb_size_cur,\n" +
        //                 "  hive_unused_tb_size_last_30d,\n" +
        //                 "  hive_unused_tb_size_last_90d,\n" +
        //                 "  hive_tb_cnt_cur,\n" +
        //                 "  hive_inused_tb_cnt_cur,\n" +
        //                 "  hive_inused_tb_cnt_last_30d,\n" +
        //                 "  hive_inused_tb_cnt_last_90d,\n" +
        //                 "  hive_unused_tb_cnt_cur,\n" +
        //                 "  hive_unused_tb_cnt_last_30d,\n" +
        //                 "  hive_unused_tb_cnt_last_90d,\n" +
        //                 "  from_unixtime(unix_timestamp(), 'yyyyMMddHHmmss') as insert_time\n" +
        //                 "from\n" +
        //                 "  ytdw.hive_info\n" +
        //                 "  cross join hdfs_info";


        // String sql =
        //         "with conversation_record AS \n" +
        //                 "    (SELECT user_id,\n" +
        //                 "        start_time,\n" +
        //                 "        time_length,\n" +
        //                 "         get_json_object(customer_json,\n" +
        //                 "         '$.shop_id') AS shop_id,call_type\n" +
        //                 "    FROM dw_hpc_sel_dx_conversation_record_d\n" +
        //                 "    WHERE dayid = '20220814'\n" +
        //                 "            AND nvl(get_json_object(customer_json, '$.shop_id'),'') != ''\n" +
        //                 "            AND conversation_type=1\n" +
        //                 "            AND user_id != '' ) \n" +
        //
        //                 "insert overwrite table st_crm_conversation_accept_detail_d partition (dayid='20220814')\n" +
        //                 "SELECT a.user_id user_id,\n" +
        //                 "         a.time time,\n" +
        //                 "         user_real_name,\n" +
        //                 "         dept_name AS name,\n" +
        //                 "         '' job_grade, nvl(all_out_count, 0) all_out_count, nvl(all_out_accept_count, 0) all_out_accept_count, concat(round(nvl(all_out_accept_count, 0)/all_out_count, 4)*100, '%') all_out_accept_count_percent, nvl(all_in_count, 0) all_in_count, nvl(all_in_accept_count, 0) all_in_accept_count, concat(round(nvl(all_in_accept_count, 0)/all_in_count, 4)*100, '%') all_in_accept_count_percent, nvl(all_count, 0) all_count, concat(round(nvl(nvl(all_out_accept_count, 0)+nvl(all_in_accept_count, 0), 0)/all_count, 4)*100, '%') all_accept_count_percent, nvl(all_sum_out_time, 0) all_sum_out_time, nvl(all_sum_in_time, 0) all_sum_in_time, nvl(all_sum_time, 0) all_sum_time\n" +
        //                 "FROM \n" +
        //                 "    (SELECT user_id,\n" +
        //                 "         from_unixtime(unix_timestamp(start_time,\n" +
        //                 "         'yyyyMMddHHmmss'), 'yyyy-MM-dd') time, count (1) all_count, round(sum(time_length)/ 60, 2) all_sum_time\n" +
        //                 "    FROM conversation_record\n" +
        //                 "    WHERE substr(start_time, 1, 6) = '202208'\n" +
        //                 "    GROUP BY  user_id, from_unixtime(unix_timestamp(start_time, 'yyyyMMddHHmmss'), 'yyyy-MM-dd') ) ALL full outer\n" +
        //                 "JOIN \n" +
        //                 "    (SELECT user_id user_id1,\n" +
        //                 "         from_unixtime(unix_timestamp(start_time,\n" +
        //                 "         'yyyyMMddHHmmss'), 'yyyy-MM-dd') time1, count (1) all_out_count, round(sum(time_length) / 60, 2) all_sum_out_time\n" +
        //                 "    FROM conversation_record\n" +
        //                 "    WHERE substr(start_time, 1, 6) = '202208'\n" +
        //                 "            AND call_type IN (2, 4)\n" +
        //                 "    GROUP BY  user_id, from_unixtime(unix_timestamp(start_time, 'yyyyMMddHHmmss'), 'yyyy-MM-dd') ) out\n" +
        //                 "    ON a.user_id = out.user_id1\n" +
        //                 "        AND a.time = out.time1 full outer\n" +
        //                 "JOIN \n" +
        //                 "    (SELECT user_id user_id2,\n" +
        //                 "         from_unixtime(unix_timestamp(start_time,\n" +
        //                 "        'yyyyMMddHHmmss'),'yyyy-MM-dd') time2, count (1) all_out_accept_count\n" +
        //                 "    FROM conversation_record\n" +
        //                 "    WHERE substr(start_time, 1, 6) = '202208'\n" +
        //                 "            AND call_type IN (2, 4)\n" +
        //                 "            AND time_length > 0\n" +
        //                 "    GROUP BY  user_id, from_unixtime(unix_timestamp(start_time,'yyyyMMddHHmmss'),'yyyy-MM-dd') ) out_accept\n" +
        //                 "    ON a.user_id = out_accept.user_id2\n" +
        //                 "        AND a.time = out_accept.time2 full outer\n" +
        //                 "JOIN \n" +
        //                 "    (SELECT user_id user_id3,\n" +
        //                 "         from_unixtime(unix_timestamp(start_time,\n" +
        //                 "        'yyyyMMddHHmmss'),'yyyy-MM-dd') time3, count (1) all_in_count, round(sum(time_length) / 60, 2) all_sum_in_time\n" +
        //                 "    FROM conversation_record\n" +
        //                 "    WHERE substr(start_time, 1, 6) = '202208'\n" +
        //                 "            AND call_type IN (1, 3)\n" +
        //                 "    GROUP BY  user_id, from_unixtime(unix_timestamp(start_time,'yyyyMMddHHmmss'),'yyyy-MM-dd') ) call_in\n" +
        //                 "    ON a.user_id = call_in.user_id3\n" +
        //                 "        AND a.time = call_in.time3 full outer\n" +
        //                 "JOIN \n" +
        //                 "    (SELECT user_id user_id4,\n" +
        //                 "         from_unixtime(unix_timestamp(start_time,\n" +
        //                 "        'yyyyMMddHHmmss'),'yyyy-MM-dd') time4, count (1) all_in_accept_count\n" +
        //                 "    FROM conversation_record\n" +
        //                 "    WHERE substr(start_time, 1, 6) = '202208'\n" +
        //                 "            AND call_type IN (1, 3)\n" +
        //                 "    GROUP BY  user_id, from_unixtime(unix_timestamp(start_time,'yyyyMMddHHmmss'),'yyyy-MM-dd') ) call_in_accept\n" +
        //                 "    ON a.user_id = call_in_accept.user_id4\n" +
        //                 "        AND a.time = call_in_accept.time4\n" +
        //                 "LEFT JOIN dim_hpc_pub_user_admin useradmin\n" +
        //                 "    ON a.user_id = useradmin.user_id\n" +
        //                 "WHERE useradmin.user_id is NOT null\n" +
        //                 "        AND useradmin.dept_id is NOT null";


        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sql;
        assertTrue(sqlparser.parse() == 0);

        TCustomSqlStatement tCustomSqlStatement = sqlparser.getSqlstatements().get(0);
        Set<String> tmpTables = List.<String>empty().toJavaSet();

        Set<String> tables = getSourceTables(tCustomSqlStatement, tmpTables);
        System.out.println("sourceTable: " + tables);

        String targetTableName = getTargetTableName(tCustomSqlStatement);
        System.out.println("targetTable: " + targetTableName);


    }

    @ParameterizedTest(name = "case-{index}: {0}")
    // @formatter:off
    @CsvSource(
            value = {
                    "ab"+ "$" + "" + "$" + "st_abtest_search_long_tail_report_d",
                    "a1" + "$" +"b1" + "$" + ""

            }
            , delimiter = '$', maxCharsPerColumn = 409600
    )
    public void testCreateSql(String sql, String sourceTable, String targetTable) {
        // @formatter:off
        String sql1 =
                "create table if not exists st_abtest_search_long_tail_report_d(\n" +
                        "  exp_code string comment '实验编号',\n" +
                        "  bucket_type int comment '分桶类型'\n" +
                        ") comment '搜索大长尾AB测试报表' partitioned by (dayid string) stored as orc";

        String sourceTable1 = "";
        String targetTable1 = "st_abtest_search_long_tail_report_d";



        // @formatter:on

        // List.of(sql1)


    }

    // all.开头的库名是不行的
    public Set<String> getSourceTables(TCustomSqlStatement stmt, Set<String> outTmpTables) {

        Function0<java.util.Set<String>> cteTableFunc = () -> {
            if (stmt instanceof TSelectSqlStatement && ((TSelectSqlStatement) stmt).getCteList() != null) {

                TCTEList cteList = ((TSelectSqlStatement) stmt).getCteList();

                java.util.Set<String> cteTableNames = List.ofAll(cteList).map(x -> {
                    return x.getTableName().toString();
                }).toJavaSet();

                return cteTableNames;
            } else if (stmt instanceof TInsertSqlStatement && ((TInsertSqlStatement) stmt).getCteList() != null) {
                TCTEList cteList = ((TInsertSqlStatement) stmt).getCteList();

                java.util.Set<String> cteTableNames = List.ofAll(cteList).map(x -> {
                    return x.getTableName().toString();
                }).toJavaSet();

                return cteTableNames;

            } else {
                return List.<String>empty().toJavaSet();
            }
        };


        Function0<Set<String>> sourceTableFunc = () -> {
            if (stmt instanceof TSelectSqlStatement) {
                Set<String> tableNames = List.ofAll(((TSelectSqlStatement) stmt).getTables())
                        // 去掉内部表 'subquery'
                        .filter(x -> x.isBaseTable())
                        .map(x -> x.getTableName().toString()).toJavaSet();

                return tableNames;
            } else {
                return List.<String>empty().toJavaSet();
            }
        };

        Set<String> allSourceTable = sourceTableFunc.get();

        Set<String> tmpTables = cteTableFunc.get();
        Set<String> mergeTmpTables = List.of(tmpTables, outTmpTables).flatMap(x -> x).toJavaSet();

        List<String> outTables = List.ofAll(allSourceTable).removeAll(List.ofAll(mergeTmpTables));

        if (stmt.getStatements().size() == 0) {
            return List.ofAll(outTables).toJavaSet();
        } else {
            java.util.List<String> internalTables = List.ofAll(stmt.getStatements()).map(x -> getSourceTables(x, mergeTmpTables)).flatMap(x -> x).toJavaList();
            Set<String> totalTables = List.of(internalTables, outTables).flatMap(x -> x).toJavaSet();
            return totalTables;
        }
    }


    public String getTargetTableName(TCustomSqlStatement stmt) {
        Function0<String> targetNameFunc = () -> {
            if (stmt.getTargetTable() != null) {
                return stmt.getTargetTable().getTableName().toString();
            } else {
                return "";
            }
        };

        return targetNameFunc.get();
    }


    public static Set<String> getTableName(TTableList tables) {
        Set<String> strings = List.ofAll(tables).map(x -> x.getTableName().toString()).toJavaSet();
        return strings;

    }
}
