package priv.hb.sample.sql.druid.lineage;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import priv.hb.sample.sql.druid.common.visitor.CustomerHiveSqlASTVisitorAdapter;

import org.junit.jupiter.api.Test;

/**
 * 测试 druid的访问者来实现 SQL中使用到了哪些字段
 * @author hubin
 * @date 2022年10月12日 11:16
 */
public class ColumnTest {

    @Test
    public void test1() {
        String sql = "select id as aid, name, address from table1 where pid>1 and pname in (select pname2 from table2)";
        // 解析SQL
        SQLStatement sqlStatement = SQLUtils.parseStatements(sql, "hive").get(0);
        CustomerHiveSqlASTVisitorAdapter customerMySqlASTVisitorAdapter = new CustomerHiveSqlASTVisitorAdapter();
        sqlStatement.accept(customerMySqlASTVisitorAdapter);

        System.out.println("columns: " + customerMySqlASTVisitorAdapter.getColumns());


        // String res = SQLUtils.toSQLString(sqlStatement);
        // System.out.println(res);
    }


    @Test
    public void test2() {
        String sql =
                "SELECT id,name\n" +
                        "FROM student s\n" +
                        "WHERE NOT EXISTS (SELECT cid FROM a.math_course c WHERE s.id = c.cid)";
        SQLStatement sqlStatement = SQLUtils.parseStatements(sql, "hive").get(0);
        CustomerHiveSqlASTVisitorAdapter customerMySqlASTVisitorAdapter = new CustomerHiveSqlASTVisitorAdapter();
        sqlStatement.accept(customerMySqlASTVisitorAdapter);

        System.out.println("columns: " + customerMySqlASTVisitorAdapter.getColumns());


    }



    @Test
    public void test3() {
        String sql =
                "SELECT a+b AS col, aa\n" +
                        "  FROM t1\n" +
                        "  UNION ALL\n" +
                        "  SELECT c+d AS col, bb\n" +
                        "  FROM t2";
        SQLStatement sqlStatement = SQLUtils.parseStatements(sql, "hive").get(0);
        CustomerHiveSqlASTVisitorAdapter customerMySqlASTVisitorAdapter = new CustomerHiveSqlASTVisitorAdapter();
        sqlStatement.accept(customerMySqlASTVisitorAdapter);

        System.out.println("columns: " + customerMySqlASTVisitorAdapter.getColumns());

        // String res = SQLUtils.toSQLString(sqlStatement);
        // System.out.println(res);

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
        SQLStatement sqlStatement = SQLUtils.parseStatements(sql, "hive").get(0);
        CustomerHiveSqlASTVisitorAdapter customerMySqlASTVisitorAdapter = new CustomerHiveSqlASTVisitorAdapter();
        sqlStatement.accept(customerMySqlASTVisitorAdapter);

        System.out.println("columns: " + customerMySqlASTVisitorAdapter.getColumns());


        // String res = SQLUtils.toSQLString(sqlStatement);
        // System.out.println(res);

    }

    @Test
    public void test5() {
        String sql =
                "select " +
                        "a.start_level, b.*\n" +
                        "from dim_level a\n" +
                        "join (select * from test) b\n" +
                        "where b.level >= a.start_level and b.level < end_level;";

        SQLStatement sqlStatement = SQLUtils.parseStatements(sql, "hive").get(0);
        CustomerHiveSqlASTVisitorAdapter customerMySqlASTVisitorAdapter = new CustomerHiveSqlASTVisitorAdapter();
        sqlStatement.accept(customerMySqlASTVisitorAdapter);

        System.out.println("columns: " + customerMySqlASTVisitorAdapter.getColumns());


        // String res = SQLUtils.toSQLString(sqlStatement);
        // System.out.println(res);

    }



    @Test
    public void test6() {
        String sql =
                "select start_level, min(max(aa.id)) AS cnt from (\n" +
                        "\tselect a.start_level, b.*\n" +
                        "\tfrom dim_level a\n" +
                        "\tjoin (select * from test) b\n" +
                        "\twhere b.level >= a.start_level and b.level < end_level\n" +
                        ")aa\n" +
                        "group by aa.start_level";

        SQLStatement sqlStatement = SQLUtils.parseStatements(sql, "hive").get(0);
        CustomerHiveSqlASTVisitorAdapter customerMySqlASTVisitorAdapter = new CustomerHiveSqlASTVisitorAdapter();
        sqlStatement.accept(customerMySqlASTVisitorAdapter);

        System.out.println("columns: " + customerMySqlASTVisitorAdapter.getColumns());


        // String res = SQLUtils.toSQLString(sqlStatement);
        // System.out.println(res);

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
        SQLStatement sqlStatement = SQLUtils.parseStatements(sql, "hive").get(0);
        CustomerHiveSqlASTVisitorAdapter customerMySqlASTVisitorAdapter = new CustomerHiveSqlASTVisitorAdapter();
        sqlStatement.accept(customerMySqlASTVisitorAdapter);

        System.out.println("columns: " + customerMySqlASTVisitorAdapter.getColumns());


        // String res = SQLUtils.toSQLString(sqlStatement);
        // System.out.println(res);

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


        SQLStatement sqlStatement = SQLUtils.parseStatements(sql.replace("all.", "aa_."), "hive").get(0);
        CustomerHiveSqlASTVisitorAdapter customerMySqlASTVisitorAdapter = new CustomerHiveSqlASTVisitorAdapter();
        sqlStatement.accept(customerMySqlASTVisitorAdapter);

        System.out.println("columns: " + customerMySqlASTVisitorAdapter.getColumns());


        // String res = SQLUtils.toSQLString(sqlStatement);
        // System.out.println(res);

    }
}
