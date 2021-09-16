package com.hb

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import java.net.URI

object HdfsUtils {

  val PRE_HDFS_NAMESPACE: String = "hcluster-pre"


  lazy val customHadoopConf: Map[String, String] = Map(
    "fs.hdfs.impl" -> "org.apache.hadoop.hdfs.DistributedFileSystem",
    //NameService列表需要包含两个集群
    "dfs.nameservices" -> s"$PRE_HDFS_NAMESPACE,$PRE_HDFS_NAMESPACE",

    //hcluster-pre为预发环境
    s"dfs.ha.automatic-failover.enabled.$PRE_HDFS_NAMESPACE" -> "true",
    s"dfs.client.failover.proxy.provider.$PRE_HDFS_NAMESPACE" -> "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider",
    s"dfs.ha.namenodes.$PRE_HDFS_NAMESPACE" -> "namenode336,namenode350",
    s"dfs.namenode.rpc-address.$PRE_HDFS_NAMESPACE.namenode336" -> "172.16.50.36:8022",
    s"dfs.namenode.rpc-address.$PRE_HDFS_NAMESPACE.namenode350" -> "172.16.50.37:8022",
    //最多failover 3次
    "dfs.client.failover.max.attempts" -> "3"
  )

  def getHadoopConfig(): Configuration = {
    val hadoopConfDir = System.getenv("HADOOP_HOME") + "/etc/hadoop"
    val hadoopConfig = new Configuration(true)
    // 下面两行,在idea里本地环境是可以不需要的,
    hadoopConfig.addResource(new Path(hadoopConfDir + "/hdfs-site.xml"))
    hadoopConfig.addResource(new Path(hadoopConfDir + "/core-site.xml"))

    // idea本地环境一定要这行,本地才会有效
    customHadoopConf.foreach(kv => hadoopConfig.set(kv._1, kv._2))
    hadoopConfig
  }

  def getPreHdfsFs(): FileSystem = {
    val hadoopConf = getHadoopConfig()
    //println("fs.hdfs.impl -> " + hadoopConf.get("fs.hdfs.impl"))
    //println("dfs.nameservices -> " + hadoopConf.get("dfs.nameservices"))
    FileSystem.get(new URI("hdfs://hcluster-pre/"), getHadoopConfig(), "hadoop")
  }

}
