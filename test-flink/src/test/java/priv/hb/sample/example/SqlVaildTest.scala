package priv.hb.sample.example

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.junit.{Before, Test}
import priv.hb.sample.util.ParserCommon

@Test
class SqlVaildTest {

  var tEnv: StreamTableEnvironment = null

  @Before
  def init(): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val setting = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build()
    tEnv = StreamTableEnvironment.create(env, setting)
  }


  @Test
  def test1(): Unit = {
    val sql1 = "select f_sequence from datagen"
    val bool = ParserCommon.ifVaildSql(sql1, tEnv)
    println("res: " + bool)
  }

  @Test
  def test2(): Unit = {
    val sql1 = "select f_sequence from2 datagen"
    val bool = ParserCommon.ifVaildSql(sql1, tEnv)
    println("res: " + bool)
  }

  @Test
  def test3(): Unit = {
    val sql1 =
      """
        |INSERT INTO part_gmv_monitor
        |SELECT
        |  CONCAT(SUBSTRING(DATE_FORMAT(TO_TIMESTAMP(pay_time),'yyyyMMddHHmm'), 0, 11 ), '0', '-', CAST(item_id AS VARCHAR), '-', CAST(product_month AS VARCHAR), '-', CAST(is_eps AS VARCHAR), '-', pro_id, '-', supply_id ) AS row_key,
        |  CAST(CONCAT(SUBSTRING(DATE_FORMAT(TO_TIMESTAMP(pay_time),'yyyyMMddHHmm'), 0, 11 ), '0') AS BIGINT) AS time_minutes,
        |  CAST(item_id AS BIGINT) AS item_id,
        |  product_month,
        |  is_eps,
        |  pro_id,
        |  supply_id,
        |
        |  SUM(pay_amount  + getPromotion(6, promotion_attr)) / CAST (100 AS DOUBLE) AS gmv,
        |  COUNT(order_id) AS order_cnt,
        |  SUM(item_count) AS item_cnt,
        |  SUM(pay_amount  + getPromotion(6, promotion_attr)) / CAST (100 AS DOUBLE) / SUM(item_count) AS avg_price,
        |  'system' AS creator,
        |  'system' AS editor,
        |  0 AS is_deleted
        |FROM (
        |  SELECT
        |    t1.item_id,
        |    t1.supply_id,
        |    t1.promotion_attr,
        |    t4.product_month,
        |
        |    t1.pay_time,
        |    t1.pay_amount,
        |    t1.order_id,
        |    t1.item_count,
        |
        |    CASE WHEN t2.store_type = 10 THEN 1 ELSE 0 END AS is_eps,  --门店是否EPS
        |    t2.provincearea_id  AS pro_id       --省份
        |  FROM (
        |    SELECT
        |      order_id,
        |      item_id,
        |      supply_id,  -- 供应商
        |      promotion_attr,
        |      getTuple(out_attr, 'item_snapshot') AS item_snapshot,
        |
        |      shop_id,
        |      proctime,
        |      pay_time,
        |      pay_amount,
        |      item_count,
        |      ROW_NUMBER() OVER (PARTITION BY order_id  ORDER BY proctime ASC) AS row_num
        |    FROM stream_yt_trade_pt_order_shop_01
        |    WHERE jsonHasKey(_change_column, '"pay_time"') = true  -- 只取支付成功那时的订单
        |      AND getTuple(out_attr, 'item_snapshot') IS NOT NULL
        |      AND DATE_FORMAT(TO_TIMESTAMP(pay_time),'yyyyMMdd') >= '20220228'
        |      AND pay_amount / item_count < 80000  -- 过滤掉单价过大的订单
        |  )t1
        |  JOIN (
        |    SELECT
        |      snapshot_no,
        |      CASE WHEN production_time <> '' THEN  CAST(REPLACE(SUBSTRING(production_time, 0, 7), '-', '') AS INT) ELSE 0 END AS product_month,
        |      ROW_NUMBER() OVER (PARTITION BY snapshot_no  ORDER BY create_time ASC) AS row_num
        |     FROM stream_yt_icp_canal_yt_icp_pt_item_base_snapshot
        |     WHERE _op_type = 'INSERT'
        |  --   AND production_time IS NOT null
        |  --   AND production_time <> ''
        |  --   AND (category_id_first IN (12, 2794, 2750, 13, 5) ) -- OR item_id IN (49973, 126536, 10717))
        |     AND DATE_FORMAT(TO_TIMESTAMP(create_time), 'yyyyMMdd') >= '20220228'
        |  )t4
        |  ON t1.item_snapshot = t4.snapshot_no
        |  LEFT JOIN shop_dim  FOR SYSTEM_TIME AS OF t1.proctime AS t2
        |  ON t1.shop_id = t2.shop_id
        |--  LEFT JOIN item_dim  FOR SYSTEM_TIME AS OF t1.proctime AS t3
        |--  ON t1.item_id = t3.id
        |
        |  WHERE
        |    t1.row_num = 1
        |    AND t4.row_num = 1
        |    AND t2.provincearea_id IS NOT NULL
        |    -- AND t3.item_type = 2
        |    -- AND t3.category_id_first = 12
        |)a
        |WHERE CONCAT(SUBSTRING(DATE_FORMAT(TO_TIMESTAMP(pay_time),'yyyyMMddHHmm'), 0, 11 ), '0', '-', CAST(item_id AS VARCHAR), '-', CAST(product_month AS VARCHAR), '-', CAST(is_eps AS VARCHAR), '-', pro_id, '-', supply_id )  IS NOT NULL
        |GROUP BY
        |  SUBSTRING(DATE_FORMAT(TO_TIMESTAMP(pay_time),'yyyyMMddHHmm'), 0, 11),
        |  item_id,
        |  product_month,
        |  is_eps,
        |  pro_id,
        |  supply_id
        |-- HAVING ( item_id IS NOT NULL AND product_month IS NOT NULL AND is_eps IS NOT NULL AND pro_id IS NOT NULL AND supply_id IS NOT NULL)
        |
        |""".stripMargin
    val bool = ParserCommon.ifVaildSql(sql1, tEnv)
    println("res: " + bool)
  }


}
