package priv.hb.sample.sql.gsql.lineage;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import io.vavr.API;
import io.vavr.Tuple2;


import static priv.hb.sample.sql.gsql.utils.Common.getLineage;
import static io.vavr.API.Tuple;

/**
 * create table 的statement的类型是 gudusoft.gsqlparser.stmt.TCreateTableSqlStatement
 * @author hubin
 * @date 2022年09月30日 09:10
 */
public class CreateTest {

    @Test
    public void test1() {

        String sql =
                "create table if not exists st_abtest_search_long_tail_report_d(\n" +
                        "  exp_code string comment '实验编号',\n" +
                        "  bucket_type int comment '分桶类型'\n" +
                        ") comment '搜索大长尾AB测试报表' partitioned by (dayid string) stored as orc;\n" +
                        "" +
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

        List<Tuple2<Set<String>, String>> lineage = getLineage(sql);

        Assert.assertEquals(lineage.size(), 2);
        Assert.assertTrue(lineage.get(0)._1.isEmpty());
        Assert.assertEquals(lineage.get(0)._2, "st_abtest_search_long_tail_report_d");
        Assert.assertEquals(lineage.get(1), Tuple(API.Set("ytdw.source_crm_contract_d").toJavaSet(), "table1.target_contract_gmv_d"));

        System.out.println(lineage);
    }


    @Test
    public void test2() {
        //@formatter:off
        String sql =
                "create view if not exists rtdw.ods_vf_crm_visit_history as " +
                        "select t2.* " +
                        "from (select *, " +
                        "             ROW_NUMBER( " +
                        "                 ) OVER (PARTITION BY id ORDER BY kafka_offset DESC " +
                        "                 ) rn " +
                        "      from (select * " +
                        "            from rtdw_mid.ods_crm_visit_history_offline " +
                        "            union all " +
                        "            select * " +
                        "            from rtdw_mid.ods_crm_visit_history_json " +
                        "           ) t1 " +
                        "     ) t2 " +
                        "where t2.rn = 1 " +
                        "  and t2.mysql_optype != 'DELETE';" +
                        "" +
                        "" +
                        "with seq_num_tab as(\n" +
                        "    select 1 as seqnum\n" +
                        "    union all\n" +
                        "    select sequnum + 1\n" +
                        "    from source_num_tab\n" +
                        "    where seqnum <100\n" +
                        ")\n" +
                        "select seqnum from seq_num_tab;\n" ;
        //@formatter:on


        List<Tuple2<Set<String>, String>> lineage = getLineage(sql);

        Assert.assertEquals(lineage.size(), 2);

        Assert.assertEquals(lineage.get(0), Tuple(API.Set("rtdw_mid.ods_crm_visit_history_offline", "rtdw_mid.ods_crm_visit_history_json").toJavaSet(), ""));
        Assert.assertEquals(lineage.get(1), Tuple(API.Set("source_num_tab").toJavaSet(), ""));

        System.out.println(lineage);
    }

}
