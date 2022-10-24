package priv.hb.sample.sql.gsql.lineage;

import priv.hb.sample.sql.gsql.lineage.visitor.MyHiveSelectStatementVisitor;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import io.vavr.collection.List;
import priv.hb.sample.sql.gsql.utils.Common;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 解析SQL中用到了哪些表的字段
 *
 * @author hubin
 * @date 2022年10月10日 16:05
 */
public class ColumnTest {
    @Test
    public void test1() {
        String sql = "select id as aid, name, address from table1 where pid>1 and pname in (select pname2 from table2)";
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sql;
        assertTrue(sqlparser.parse() == 0);

        TStatementList sqlstatements = sqlparser.getSqlstatements();
        TCustomSqlStatement tCustomSqlStatement = sqlstatements.get(0);

        MyHiveSelectStatementVisitor myHiveSelectStatementVisitor = new MyHiveSelectStatementVisitor();

        tCustomSqlStatement.accept(myHiveSelectStatementVisitor);
        System.out.println(myHiveSelectStatementVisitor.getColumns());
        System.out.println(myHiveSelectStatementVisitor.getColumnAndSourceTables());
        Assert.assertEquals(myHiveSelectStatementVisitor.getColumns(), List.of("id", "name", "address", "pid", "pname", "pname2").toJavaList());

    }

    @Test
    public void test2() {
        String sql =
                "SELECT id,name\n" +
                        "FROM student s\n" +
                        "WHERE NOT EXISTS (SELECT cid FROM a.math_course c WHERE s.id = c.cid)";
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sql;
        assertTrue(sqlparser.parse() == 0);

        TStatementList sqlstatements = sqlparser.getSqlstatements();
        TCustomSqlStatement tCustomSqlStatement = sqlstatements.get(0);

        MyHiveSelectStatementVisitor myHiveSelectStatementVisitor = new MyHiveSelectStatementVisitor();

        tCustomSqlStatement.accept(myHiveSelectStatementVisitor);
        System.out.println(myHiveSelectStatementVisitor.getColumns());
        System.out.println(myHiveSelectStatementVisitor.getColumnAndSourceTables());
        Assert.assertEquals(myHiveSelectStatementVisitor.getColumns(), List.of("id", "name", "cid", "s.id", "c.cid").toJavaList());
    }


    @Test
    public void test3() {
        String sql =
                "SELECT a+b AS col, aa\n" +
                        "  FROM t1\n" +
                        "  UNION ALL\n" +
                        "  SELECT c+d AS col, bb\n" +
                        "  FROM t2";
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sql;

        sqlparser.parse();
        System.out.println(Common.getErrors(sqlparser));
        assertTrue(sqlparser.parse() == 0);

        TStatementList sqlstatements = sqlparser.getSqlstatements();
        TCustomSqlStatement tCustomSqlStatement = sqlstatements.get(0);

        MyHiveSelectStatementVisitor myHiveSelectStatementVisitor = new MyHiveSelectStatementVisitor();

        tCustomSqlStatement.accept(myHiveSelectStatementVisitor);
        System.out.println(myHiveSelectStatementVisitor.getColumns());
        System.out.println(myHiveSelectStatementVisitor.getColumnAndSourceTables());
        Assert.assertEquals(myHiveSelectStatementVisitor.getColumns(), List.of("a", "b", "aa", "c", "d", "bb").toJavaList());
    }


    @Test
    public void test4() {
        String sql =
                "SELECT t3.col\n" +
                        "FROM (\n" +
                        "  SELECT a+b AS col, aa\n" +
                        "  FROM t1\n" +
                        "  UNION ALL\n" +
                        "  SELECT c+d AS col, bb\n" +
                        "  FROM t2\n" +
                        ") t3";
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sql;

        sqlparser.parse();
        System.out.println(Common.getErrors(sqlparser));
        assertTrue(sqlparser.parse() == 0);

        TStatementList sqlstatements = sqlparser.getSqlstatements();
        TCustomSqlStatement tCustomSqlStatement = sqlstatements.get(0);

        MyHiveSelectStatementVisitor myHiveSelectStatementVisitor = new MyHiveSelectStatementVisitor();

        tCustomSqlStatement.accept(myHiveSelectStatementVisitor);
        System.out.println(myHiveSelectStatementVisitor.getColumns());
        System.out.println(myHiveSelectStatementVisitor.getColumnAndSourceTables());
        Assert.assertEquals(myHiveSelectStatementVisitor.getColumns(), List.of("t3.col", "a", "b", "aa", "c", "d", "bb").toJavaList());
    }


    @Test
    public void test5() {
        String sql =
                "select " +
                        "a.start_level, b.*\n" +
                        "from dim_level a\n" +
                        "join (select * from test) b\n" +
                        "where b.level >= a.start_level and b.level < end_level;";
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sql;

        sqlparser.parse();
        System.out.println(Common.getErrors(sqlparser));
        assertTrue(sqlparser.parse() == 0);

        TStatementList sqlstatements = sqlparser.getSqlstatements();
        TCustomSqlStatement tCustomSqlStatement = sqlstatements.get(0);

        MyHiveSelectStatementVisitor myHiveSelectStatementVisitor = new MyHiveSelectStatementVisitor();

        tCustomSqlStatement.accept(myHiveSelectStatementVisitor);
        System.out.println(myHiveSelectStatementVisitor.getColumns());
        System.out.println(myHiveSelectStatementVisitor.getColumnAndSourceTables());
        Assert.assertEquals(myHiveSelectStatementVisitor.getColumns(), List.of("a.start_level", "b.*", "*", "b.level", "a.start_level", "b.level", "end_level").toJavaList());
    }


    @Test
    public void test6() {
        String sql =
                "select start_level, min(max(aa.id)) AS cnt from (\n" +
                        "\tselect a.start_level, b.*\n" +
                        "\tfrom dim_level a\n" +
                        "\tjoin (select * from (select * from test) bb ) b\n" +
                        "\twhere b.level >= a.start_level and b.level < end_level\n" +
                        ")aa\n" +
                        "group by aa.start_level";
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sql;

        sqlparser.parse();
        System.out.println(Common.getErrors(sqlparser));
        assertTrue(sqlparser.parse() == 0);

        TStatementList sqlstatements = sqlparser.getSqlstatements();
        TCustomSqlStatement tCustomSqlStatement = sqlstatements.get(0);

        MyHiveSelectStatementVisitor myHiveSelectStatementVisitor = new MyHiveSelectStatementVisitor();

        tCustomSqlStatement.accept(myHiveSelectStatementVisitor);
        System.out.println(myHiveSelectStatementVisitor.getColumns());
        System.out.println(myHiveSelectStatementVisitor.getColumnAndSourceTables());
        Assert.assertEquals(myHiveSelectStatementVisitor.getColumns(), List.of("start_level", "aa.id", "a.start_level", "b.*", "*", "*", "b.level", "a.start_level", "b.level", "end_level").toJavaList());
    }


    @Test
    public void test7() {
        //@formatter:off
        String sql =
                "with tmp1 as ( select key1 from srcTable1 where id = '5'),\n" +
                "     tmp2 as ( select key2 from srcTable2 where id = '5')\n" +
                "select *\n" +
                "from tmp1;\n";
        //@formatter:on

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sql;

        sqlparser.parse();
        System.out.println(Common.getErrors(sqlparser));
        assertTrue(sqlparser.parse() == 0);

        TStatementList sqlstatements = sqlparser.getSqlstatements();
        TCustomSqlStatement tCustomSqlStatement = sqlstatements.get(0);

        MyHiveSelectStatementVisitor myHiveSelectStatementVisitor = new MyHiveSelectStatementVisitor();

        tCustomSqlStatement.accept(myHiveSelectStatementVisitor);
        System.out.println(myHiveSelectStatementVisitor.getColumns());
        System.out.println(myHiveSelectStatementVisitor.getColumnAndSourceTables());
        Assert.assertEquals(myHiveSelectStatementVisitor.getColumns(), List.of("key1", "id", "key2", "id", "*").toJavaList());

    }


    @Test
    public void test8() {
        // @formatter:off
        String sql =
                "with conversation_record AS \n" +
                        "    (SELECT user_id,\n" +
                        "        start_time,\n" +
                        "        time_length,\n" +
                        "         get_json_object(customer_json,\n" +
                        "         '$.shop_id') AS shop_id, call_type\n" +
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


        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = sql.replace("all.", "all_.");

        sqlparser.parse();
        System.out.println(Common.getErrors(sqlparser));
        assertTrue(sqlparser.parse() == 0);

        TStatementList sqlstatements = sqlparser.getSqlstatements();
        TCustomSqlStatement tCustomSqlStatement = sqlstatements.get(0);

        MyHiveSelectStatementVisitor myHiveSelectStatementVisitor = new MyHiveSelectStatementVisitor();

        tCustomSqlStatement.accept(myHiveSelectStatementVisitor);
        System.out.println(myHiveSelectStatementVisitor.getColumns());
        System.out.println(myHiveSelectStatementVisitor.getColumnAndSourceTables());

    }
}
