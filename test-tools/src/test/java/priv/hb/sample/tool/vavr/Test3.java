package priv.hb.sample.tool.vavr;

import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Queue;
import io.vavr.concurrent.Future;

public class Test3 {

    @Test
    public void testLog() {
        String log = "依赖任务全部到位，开始执行\n" +
                "HIRAC# 2019-11-20 01:58:18 进入离线任务队列\n" +
                "HIRAC# 2019-11-20 01:59:40 开始运行\n" +
                "HIRAC# 2019-11-20 01:59:40 开始执行前置处理单元DownLoadJob\n" +
                "HIRAC# 2019-11-20 01:59:40 前置处理单元DownLoadJob处理完毕\n" +
                "HIRAC# 2019-11-20 01:59:40 开始执行核心job\n" +
                "HIRAC# 2019-11-20 01:59:40 dos2unix file:/alidata/server/hirac/work-dir/2019-11-20/481862/1574186380481.sh\n" +
                "CONSOLE# 2019-11-20 01:59:40 dos2unix: converting file /alidata/server/hirac/work-dir/2019-11-20/481862/1574186380481.sh to Unix format ...\n" +
                "CONSOLE# 2019-11-20 01:59:40 JAVA_HOME:/alidata/server/java\n" +
                "CONSOLE# 2019-11-20 01:59:40 dbconfig=\n" +
                "CONSOLE# 2019-11-20 01:59:40 date=2019年 11月 20日 星期三 01:59:40 CST\n" +
                "CONSOLE# 2019-11-20 01:59:40 job=dwd_settle_coupon_original_detail_d\n" +
                "CONSOLE# 2019-11-20 01:59:40 date_time=20191119\n" +
                "CONSOLE# 2019-11-20 01:59:40 ----------------start--------------------\n" +
                "CONSOLE# 2019-11-20 01:59:40 DIR=/alidata/workspace/yt_bigdata/edp/data_offline\n" +
                "CONSOLE# 2019-11-20 01:59:40 ods_source_table=ods_t_settle_coupon_original_detail_d target_table=dwd_settle_coupon_original_detail_d\n" +
                "CONSOLE# 2019-11-20 01:59:40 datadate=20191119\n" +
                "CONSOLE# 2019-11-20 01:59:40 create_sql=create table if not exists dwd_settle_coupon_original_detail_d ( id bigint comment '主键ID', coupon_original_id bigint comment '优惠券源数据ID', coupon_bill_id bigint comment '优惠券账单ID', coupon_id bigint comment '优惠券ID', shop_id string comment '门店ID', coupon_owner_id bigint comment '优惠券拥有ID', coupon_amount bigint comment '优惠券金额', coupon_valid_start_time string comment '有效期开始时间', coupon_valid_end_time string comment '有效期结束时间', coupon_bear_id string comment '承担者ID', settle_time string comment '账单时间', expense_type tinyint comment '费用类型', is_undo tinyint comment '撤回表示', settle_status tinyint comment '结算状态', version int comment '版本号', snapshot_version int comment '快照版本号', remark string comment '备注', is_deleted tinyint comment '删除标识', creator string comment '创建者', editor string comment '编辑者', create_time string comment '创建时间', edit_time string comment '编辑时间') partitioned by (dayid string) row format delimited fields terminated by '\\001' stored as orc location '/dw/ytdw/dwd/dwd_settle_coupon_original_detail_d';\n" +
                "CONSOLE# 2019-11-20 01:59:40 select_sql=select id, coupon_original_id, coupon_bill_id, coupon_id, shop_id, coupon_owner_id, coupon_amount, from_unixtime(unix_timestamp(coupon_valid_start_time),'yyyyMMddHHmmss') as coupon_valid_start_time, from_unixtime(unix_timestamp(coupon_valid_end_time),'yyyyMMddHHmmss') as coupon_valid_end_time, coupon_bear_id, from_unixtime(unix_timestamp(settle_time),'yyyyMMddHHmmss') as settle_time, expense_type, is_undo, settle_status, version, snapshot_version, remark, is_deleted, creator, editor, from_unixtime(unix_timestamp(create_time),'yyyyMMddHHmmss') as create_time, from_unixtime(unix_timestamp(edit_time),'yyyyMMddHHmmss') as edit_time from ods_t_settle_coupon_original_detail_d where dayid='20191119';\n" +
                "CONSOLE# 2019-11-20 01:59:41 Java HotSpot(TM) 64-Bit Server VM warning: Using incremental CMS is deprecated and will likely be removed in a future release\n" +
                "CONSOLE# 2019-11-20 01:59:44 Logging initialized using configuration in jar:file:/opt/cloudera/parcels/CDH-5.13.2-1.cdh5.13.2.p0.3/jars/hive-common-1.1.0-cdh5.13.2.jar!/hive-log4j.properties\n" +
                "CONSOLE# 2019-11-20 01:59:47 POSTHOOK: query: use ytdw\n" +
                "CONSOLE# 2019-11-20 01:59:47 POSTHOOK: type: SWITCHDATABASE\n" +
                "CONSOLE# 2019-11-20 01:59:47 POSTHOOK: Input: database:ytdw\n" +
                "CONSOLE# 2019-11-20 01:59:47 OK\n" +
                "CONSOLE# 2019-11-20 01:59:47 Time taken: 2.263 seconds\n" +
                "CONSOLE# 2019-11-20 01:59:47 POSTHOOK: query: create table if not exists dwd_settle_coupon_original_detail_d\n" +
                "CONSOLE# 2019-11-20 01:59:47 (\n" +
                "CONSOLE# 2019-11-20 01:59:47 id bigint comment '主键ID',\n" +
                "CONSOLE# 2019-11-20 01:59:47 coupon_original_id bigint comment '优惠券源数据ID',\n" +
                "CONSOLE# 2019-11-20 01:59:47 coupon_bill_id bigint comment '优惠券账单ID',\n" +
                "CONSOLE# 2019-11-20 01:59:47 coupon_id bigint comment '优惠券ID',\n" +
                "CONSOLE# 2019-11-20 01:59:47 shop_id string comment '门店ID',\n" +
                "CONSOLE# 2019-11-20 01:59:47 coupon_owner_id bigint comment '优惠券拥有ID',\n" +
                "CONSOLE# 2019-11-20 01:59:47 coupon_amount bigint comment '优惠券金额',\n" +
                "CONSOLE# 2019-11-20 01:59:47 coupon_valid_start_time string comment '有效期开始时间',\n" +
                "CONSOLE# 2019-11-20 01:59:47 coupon_valid_end_time string comment '有效期结束时间',\n" +
                "CONSOLE# 2019-11-20 01:59:47 coupon_bear_id string comment '承担者ID',\n" +
                "CONSOLE# 2019-11-20 01:59:47 settle_time string comment '账单时间',\n" +
                "CONSOLE# 2019-11-20 01:59:47 expense_type tinyint comment '费用类型',\n" +
                "CONSOLE# 2019-11-20 01:59:47 is_undo tinyint comment '撤回表示',\n" +
                "CONSOLE# 2019-11-20 01:59:47 settle_status tinyint comment '结算状态',\n" +
                "CONSOLE# 2019-11-20 01:59:47 version int comment '版本号',\n" +
                "CONSOLE# 2019-11-20 01:59:47 snapshot_version int comment '快照版本号',\n" +
                "CONSOLE# 2019-11-20 01:59:47 remark string comment '备注',\n" +
                "CONSOLE# 2019-11-20 01:59:47 is_deleted tinyint comment '删除标识',\n" +
                "CONSOLE# 2019-11-20 01:59:47 creator string comment '创建者',\n" +
                "CONSOLE# 2019-11-20 01:59:47 editor string comment '编辑者',\n" +
                "CONSOLE# 2019-11-20 01:59:47 create_time string comment '创建时间',\n" +
                "CONSOLE# 2019-11-20 01:59:47 edit_time string comment '编辑时间')\n" +
                "CONSOLE# 2019-11-20 01:59:47 partitioned by (dayid string)\n" +
                "CONSOLE# 2019-11-20 01:59:47 row format delimited fields terminated by '\\001'\n" +
                "CONSOLE# 2019-11-20 01:59:47 stored as orc\n" +
                "CONSOLE# 2019-11-20 01:59:47 location '/dw/ytdw/dwd/dwd_settle_coupon_original_detail_d'\n" +
                "CONSOLE# 2019-11-20 01:59:47 POSTHOOK: type: CREATETABLE\n" +
                "CONSOLE# 2019-11-20 01:59:47 POSTHOOK: Input: hdfs://hcluster/dw/ytdw/dwd/dwd_settle_coupon_original_detail_d\n" +
                "CONSOLE# 2019-11-20 01:59:47 OK\n" +
                "CONSOLE# 2019-11-20 01:59:47 Time taken: 0.269 seconds\n" +
                "CONSOLE# 2019-11-20 01:59:48 Query ID = hadoop_20191120015959_78372175-fb6d-4aa9-a922-168729f32181\n" +
                "CONSOLE# 2019-11-20 01:59:48 Total jobs = 3\n" +
                "CONSOLE# 2019-11-20 01:59:48 Launching Job 1 out of 3\n" +
                "CONSOLE# 2019-11-20 01:59:48 In order to change the average load for a reducer (in bytes):\n" +
                "CONSOLE# 2019-11-20 01:59:48 set hive.exec.reducers.bytes.per.reducer=\n" +
                "CONSOLE# 2019-11-20 01:59:48 In order to limit the maximum number of reducers:\n" +
                "CONSOLE# 2019-11-20 01:59:48 set hive.exec.reducers.max=\n" +
                "CONSOLE# 2019-11-20 01:59:48 In order to set a constant number of reducers:\n" +
                "CONSOLE# 2019-11-20 01:59:48 set mapreduce.job.reduces=\n" +
                "CONSOLE# 2019-11-20 01:59:56 Starting Spark Job = 94e569dd-744c-43ae-ab29-2f193821ba0f\n" +
                "CONSOLE# 2019-11-20 02:00:03 Running with YARN Application = application_1570849247040_386633\n" +
                "CONSOLE# 2019-11-20 02:00:03 Kill Command = /opt/cloudera/parcels/CDH-5.13.2-1.cdh5.13.2.p0.3/lib/hadoop/bin/yarn application -kill application_1570849247040_386633\n" +
                "CONSOLE# 2019-11-20 02:00:03 Query Hive on Spark job[0] stages:\n" +
                "CONSOLE# 2019-11-20 02:00:03 0\n" +
                "CONSOLE# 2019-11-20 02:00:03 Status: Running (Hive on Spark job[0])\n" +
                "CONSOLE# 2019-11-20 02:00:03 Job Progress Format\n" +
                "CONSOLE# 2019-11-20 02:00:03 CurrentTime StageId_StageAttemptId: SucceededTasksCount(+RunningTasksCount-FailedTasksCount)/TotalTasksCount [StageCost]\n" +
                "CONSOLE# 2019-11-20 02:00:03 2019-11-20 02:00:03,917 Stage-0_0: 0(+1)/1\n" +
                "CONSOLE# 2019-11-20 02:00:06 2019-11-20 02:00:06,956 Stage-0_0: 0(+1)/1\n" +
                "CONSOLE# 2019-11-20 02:00:07 2019-11-20 02:00:07,965 Stage-0_0: 1/1 Finished\n" +
                "CONSOLE# 2019-11-20 02:00:07 Status: Finished successfully in 11.11 seconds\n" +
                "CONSOLE# 2019-11-20 02:00:08 Stage-4 is selected by condition resolver.\n" +
                "CONSOLE# 2019-11-20 02:00:08 Stage-3 is filtered out by condition resolver.\n" +
                "CONSOLE# 2019-11-20 02:00:08 Stage-5 is filtered out by condition resolver.\n" +
                "CONSOLE# 2019-11-20 02:00:10 Moving data to: hdfs://hcluster/dw/ytdw/dwd/dwd_settle_coupon_original_detail_d/dayid=20191119/.hive-staging_hive_2019-11-20_01-59-47_462_8686768874887078552-1/-ext-10000\n" +
                "CONSOLE# 2019-11-20 02:00:10 Loading data to table ytdw.dwd_settle_coupon_original_detail_d partition (dayid=20191119)\n" +
                "CONSOLE# 2019-11-20 02:00:11 Partition ytdw.dwd_settle_coupon_original_detail_d{dayid=20191119} stats: [numFiles=1, numRows=28775, totalSize=809738, rawDataSize=29492442]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: query: insert overwrite table dwd_settle_coupon_original_detail_d partition(dayid='20191119')\n" +
                "CONSOLE# 2019-11-20 02:00:11 select\n" +
                "CONSOLE# 2019-11-20 02:00:11 id,\n" +
                "CONSOLE# 2019-11-20 02:00:11 coupon_original_id,\n" +
                "CONSOLE# 2019-11-20 02:00:11 coupon_bill_id,\n" +
                "CONSOLE# 2019-11-20 02:00:11 coupon_id,\n" +
                "CONSOLE# 2019-11-20 02:00:11 shop_id,\n" +
                "CONSOLE# 2019-11-20 02:00:11 coupon_owner_id,\n" +
                "CONSOLE# 2019-11-20 02:00:11 coupon_amount,\n" +
                "CONSOLE# 2019-11-20 02:00:11 from_unixtime(unix_timestamp(coupon_valid_start_time),'yyyyMMddHHmmss') as coupon_valid_start_time,\n" +
                "CONSOLE# 2019-11-20 02:00:11 from_unixtime(unix_timestamp(coupon_valid_end_time),'yyyyMMddHHmmss') as coupon_valid_end_time,\n" +
                "CONSOLE# 2019-11-20 02:00:11 coupon_bear_id,\n" +
                "CONSOLE# 2019-11-20 02:00:11 from_unixtime(unix_timestamp(settle_time),'yyyyMMddHHmmss') as settle_time,\n" +
                "CONSOLE# 2019-11-20 02:00:11 expense_type,\n" +
                "CONSOLE# 2019-11-20 02:00:11 is_undo,\n" +
                "CONSOLE# 2019-11-20 02:00:11 settle_status,\n" +
                "CONSOLE# 2019-11-20 02:00:11 version,\n" +
                "CONSOLE# 2019-11-20 02:00:11 snapshot_version,\n" +
                "CONSOLE# 2019-11-20 02:00:11 remark,\n" +
                "CONSOLE# 2019-11-20 02:00:11 is_deleted,\n" +
                "CONSOLE# 2019-11-20 02:00:11 creator,\n" +
                "CONSOLE# 2019-11-20 02:00:11 editor,\n" +
                "CONSOLE# 2019-11-20 02:00:11 from_unixtime(unix_timestamp(create_time),'yyyyMMddHHmmss') as create_time,\n" +
                "CONSOLE# 2019-11-20 02:00:11 from_unixtime(unix_timestamp(edit_time),'yyyyMMddHHmmss') as edit_time from ods_t_settle_coupon_original_detail_d\n" +
                "CONSOLE# 2019-11-20 02:00:11 where dayid='20191119'\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: type: QUERY\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Input: ytdw@ods_t_settle_coupon_original_detail_d\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Input: ytdw@ods_t_\n" +
                "CONSOLE# 2019-11-20 02:00:11 settle_coupon_original_detail_d@dayid=20191119\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Output: ytdw@dwd_settle_coupon_original_detail_d@dayid=20191119\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).coupon_amount SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:coupon_amount, type:bigint, comment:优惠券金额), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).coupon_bear_id SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:coupon_bear_id, type:string, comment:承担者ID), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).coupon_bill_id SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:coupon_bill_id, type:bigint, comment:优惠券账单ID), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).coupon_id SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:coupon_id, type:bigint, comment:优惠券ID), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).coupon_original_id SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:coupon_original_id, type:bigint, comment:优惠券源数据ID), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).coupon_owner_id SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:coupon_owner_id, type:bigint, comment:优惠券拥有ID), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).coupon_valid_end_time EXPRESSION [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:coupon_valid_end_time, type:string, comment:有效期结束时间), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).coupon_valid_start_time EXPRESSION [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:coupon_valid_start_time, type:string, comment:有效期开始时间), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).create_time EXPRESSION [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:create_time, type:string, comment:创建时间), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).creator SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:creator, type:string, comment:创建者), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).edit_time EXPRESSION [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:edit_time, type:string, comment:编辑时间), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).editor SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:editor, type:string, comment:编辑者), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).expense_type SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:expense_type, type:tinyint, comment:费用类型), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).id SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:id, type:bigint, comment:主键ID), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).is_deleted SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:is_deleted, type:tinyint, comment:删除标识), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).is_undo SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:is_undo, type:tinyint, comment:撤回表示), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).remark SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:remark, type:string, comment:备注), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).settle_status SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:settle_status, type:tinyint, comment:结算状态), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).settle_time EXPRESSION [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:settle_time, type:string, comment:账单时间), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).shop_id SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:shop_id, type:string, comment:门店ID), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).snapshot_version SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:snapshot_version, type:int, comment:快照版本号), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 POSTHOOK: Lineage: dwd_settle_coupon_original_detail_d PARTITION(dayid=20191119).version SIMPLE [(ods_t_settle_coupon_original_detail_d)ods_t_settle_coupon_original_detail_d.FieldSchema(name:version, type:int, comment:版本号), ]\n" +
                "CONSOLE# 2019-11-20 02:00:11 OK\n" +
                "CONSOLE# 2019-11-20 02:00:11 Time taken: 24.509 seconds\n" +
                "CONSOLE# 2019-11-20 02:00:11 WARN: The method class org.apache.commons.logging.impl.SLF4JLogFactory#release() was invoked.\n" +
                "CONSOLE# 2019-11-20 02:00:11 WARN: Please see http://www.slf4j.org/codes.html#release for an explanation.\n" +
                "HIRAC# 2019-11-20 02:00:12 核心job处理完毕\n" +
                "HIRAC# 2019-11-20 02:00:12 exitCode = 0";




        String[] t = log.split("\n");
        List<String> log1 = List.of(t);

        List<String> log2 = log1.filter(x -> x.contains("CONSOLE"));
        System.out.println(log2);


        List<Tuple2<String, Integer>> log3 = log2.zipWithIndex();
        List<Integer> log4 = log3.filter(x -> x._1().contains("POSTHOOK: type: QUERY")).map(x -> x._2());
        List<Integer> log5 = log3.filter(x -> x._1().contains("POSTHOOK: Output:")).map(x -> x._2());


        List<Integer> l6 = log4.appendAll(log5);

        List<Tuple2<Integer, Integer>> log6 = l6.init().zip(l6.tail());


        System.out.println(log6);

        List<String> log7 = log6.map(logs -> {
                    List<String> tt = log2.zipWithIndex().filter(x -> x._2 >= logs._1 && x._2 <= logs._2).map(x -> x._1);
                    return tt.mkString("\n");
                }
        );


        System.out.println("--------");
        System.out.println(log7.mkString("\n"));

        Queue<Integer> queue = Queue.of(1, 2, 3)
                .enqueue(4)
                .enqueue(5);

        System.out.println(queue);

        System.out.println("version1");


    }

    @Test
    public void testFutureFailure() {
        final String word = "hello world";
        io.vavr.concurrent.Future
                .of(Executors.newFixedThreadPool(1), () -> word)
                .onFailure(throwable -> Assert.fail("不应该走到 failure 分支"))
                .onSuccess(result -> Assert.assertEquals(word, result));
    }

    @Test
    public void testFutureSuccess() {
        io.vavr.concurrent.Future
                .of(Executors.newFixedThreadPool(1), () -> {
                    throw new RuntimeException();
                })
                .onFailure(throwable -> Assert.assertTrue(throwable instanceof RuntimeException))
                .onSuccess(result -> Assert.fail("不应该走到 success 分支"));


        final String word = "hello world";
        Future<String> of = Future
                .of(Executors.newFixedThreadPool(1), () -> word);



    }





}
