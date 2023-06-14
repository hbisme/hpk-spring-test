package priv.hb.sample.example

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api.bridge.scala.internal.StreamTableEnvironmentImpl
import org.apache.flink.table.delegation.Parser
import org.apache.flink.table.operations.Operation
import org.apache.flink.table.planner.delegation.ParserImpl
import priv.hb.sample.util.ParserCommon

object SqlVaildApp1 {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val setting = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build()
    val tEnv: StreamTableEnvironment = StreamTableEnvironment.create(env, setting)


    val sourceDDL =
      """
        |CREATE TABLE datagen (
        |  f_sequence INT,
        |  f_random INT,
        |  job AS CONCAT('job_', CAST(mod(f_random, 10) AS VARCHAR) ),
        |  instance AS CONCAT('instance_', CAST(mod(f_random, 1) AS VARCHAR) ),
        |  host1 AS CONCAT('host1_', f_random_str1 ),
        |  app AS CONCAT('application_', f_random_str2),
        |  code AS CONCAT('code_', f_random_str3),
        |  host2 AS CONCAT('host2_', f_random_str1 ),
        |  rt AS f_random,
        |  isgt100 AS IF(f_random> 100, 'true', 'false'),
        |
        |  f_random_str1 VARCHAR,
        |  f_random_str2 VARCHAR,
        |  f_random_str3 VARCHAR,
        |  f_random_str4 VARCHAR,
        |  f_random_str5 VARCHAR,
        |  __f_random_str6 VARCHAR,
        |  ts AS localtimestamp,
        |  WATERMARK FOR ts AS ts
        |
        |) WITH (
        |  'connector' = 'datagen',   -- 指定类型为生成的数据源
        |  'rows-per-second'='5',     -- 每秒产生的数据数量
        |  'fields.f_sequence.kind'='sequence',  -- f_sequence 字段产生有序的序列
        |  'fields.f_sequence.start'='1',
        |  'fields.f_sequence.end'='1000000',
        |  'fields.f_random.min'='1',           -- f_random设置
        |  'fields.f_random.max'='1000',
        |
        |  'fields.f_random_str1.length'='1',  -- f_random_str1随机字段的长度
        |  'fields.f_random_str2.length'='1',
        |  'fields.f_random_str3.length'='1',
        |  'fields.f_random_str4.length'='1',
        |  'fields.f_random_str5.length'='1',
        |  'fields.__f_random_str6.length'='1'
        |)
        |""".stripMargin

    val sql1 = "select f_sequence from datagen"


    val impl: StreamTableEnvironmentImpl = tEnv.asInstanceOf[StreamTableEnvironmentImpl]
    val parser: Parser = impl.getParser




    val bool = ParserCommon.ifVaildSql(sql1, parser)
    println("res: " + bool)



    println("end")

  }
}
