package priv.hb.sample.sql.druid.lineage2;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;

import priv.hb.sample.sql.druid.common.visitor.HiveLineageVisitor;

import org.junit.Test;

/**
 * 测试SQL解析中的CREATE TABLE 和 CREATE VIEW 语句
 * 得到源表,临时表和结果表
 *
 * @author hubin
 * @date 2022年09月15日 09:25
 */
public class CreateTableViewTest {
    @Test
    public void test1() {

        // @formatter:off
        String sql =
                "create external table if not exists rtdw_mid.ods_crm_visit_history_json( " +
                        "id bigint, " +
                        "shop_id string, " +
                        "mysql_optype string comment '操作类型标记，删除delete' )" +
                        " ROW FORMAT SERDE 'org.apache.hive.hcatalog.data.JsonSerDe' STORED AS TEXTFILE" +
                        " LOCATION '/rt_data/mysql_canal_json/crm/ods_crm_visit_history_json_0'";
        // @formatter:on

        // 解析SQL
        HiveStatementParser parser = new HiveStatementParser(sql);
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
                "create table if not exists st_abtest_search_long_tail_report_d("
                        + "     exp_code string comment '实验编号',"
                        + "     bucket_type int comment '分桶类型') comment '搜索大长尾AB测试报表'"
                        + "   partitioned by (dayid string) stored as orc";
        // @formatter:on

        // 解析SQL
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
                        "  and t2.mysql_optype != 'DELETE';";
        // @formatter:on

        // 解析SQL
        HiveStatementParser parser = new HiveStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();

        HiveLineageVisitor hiveLineageVisitor = new HiveLineageVisitor();
        sqlStatement.accept(hiveLineageVisitor);

        System.out.println(hiveLineageVisitor.getSource());
        System.out.println(hiveLineageVisitor.getTarget());
        System.out.println(hiveLineageVisitor.getTemporaries());
    }


    @Test
    public void test5() {
        // @formatter:off
        String sql =
                "create view if not exists rtdw.ods_v_crm_visit_history As\n" +
                        "select id,\n" +
                        "       shop_id,\n" +
                        "       visit_type,\n" +
                        "       visit_time,\n" +
                        "       visit_aims,\n" +
                        "       visit_result,\n" +
                        "       shop_problem,\n" +
                        "       solution,\n" +
                        "       other,\n" +
                        "       is_deleted,\n" +
                        "       create_time,\n" +
                        "       edit_time,\n" +
                        "       creator,\n" +
                        "       editor,\n" +
                        "       pic_url,\n" +
                        "       distance,\n" +
                        "       position,\n" +
                        "       longitude,\n" +
                        "       latitude,\n" +
                        "       cooperate_type,\n" +
                        "       visit_name,\n" +
                        "       visit_phone,\n" +
                        "       visit_mode,\n" +
                        "       shop_pro_id,\n" +
                        "       shop_city_id,\n" +
                        "       shop_area_id,\n" +
                        "       b_mature_level,\n" +
                        "       visit_status,\n" +
                        "       plan_id,\n" +
                        "       with_flag,\n" +
                        "       with_user_id,\n" +
                        "       shop_street_id,\n" +
                        "       conversation_code,\n" +
                        "       visit_aims_type,\n" +
                        "       front_photo,\n" +
                        "       inner_photo,\n" +
                        "       poster,\n" +
                        "       screenshot,\n" +
                        "       other_photo,\n" +
                        "       subject_ids,\n" +
                        "       verify_status,\n" +
                        "       visit_record_id\n" +
                        "FROM (SELECT *,\n" +
                        "             ROW_NUMBER(\n" +
                        "                 ) OVER (PARTITION BY id ORDER BY kafka_offset DESC\n" +
                        "                 ) as rn\n" +
                        "      FROM rtdw_mid.ods_crm_visit_history_json\n" +
                        "     ) t\n" +
                        "where t.rn = 1\n" +
                        "  and mysql_optype != 'DELETE';";
        // @formatter:on

        // 解析SQL
        HiveStatementParser parser = new HiveStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();

        HiveLineageVisitor hiveLineageVisitor = new HiveLineageVisitor();
        sqlStatement.accept(hiveLineageVisitor);

        System.out.println(hiveLineageVisitor.getSource());
        System.out.println(hiveLineageVisitor.getTarget());
        System.out.println(hiveLineageVisitor.getTemporaries());


    }


}
