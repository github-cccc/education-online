package com.githubzhu.member.controller

import com.githubzhu.member.service.EtlDataService
import com.githubzhu.util.HiveUtil
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author: github_zhu
 * @Describtion:
 * @Date:Created in 2020/6/19 16:04
 * @ModifiedBy:
 *
 */
object DwdMemberController {
  def main(args: Array[String]): Unit = {

    System.setProperty("HADOOP_USER_NAME", "root")
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("dwd_member_import").setMaster("local[*]")

    val sparkSession = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()
    val ssc = sparkSession.sparkContext
    ssc.hadoopConfiguration.set("fs.defaultFS", "hdfs://mycluster")
    ssc.hadoopConfiguration.set("dfs.nameservices", "mycluster")
    HiveUtil.openDynamicPartition(sparkSession) //开启动态分区
    HiveUtil.openCompression(sparkSession) //开启压缩
    //    HiveUtil.useSnappyCompression(sparkSession) //使用snappy压缩

    //对用户原始数据进行清洗，存入到dwd 层数据表
    EtlDataService.etlBaseAdLog(ssc, sparkSession) //导入基础广告表数据
    EtlDataService.etlBaseWebSiteLog(ssc, sparkSession) //导入基础网站表数据
    EtlDataService.etlMemberLog(ssc, sparkSession) //清洗用户数据
    EtlDataService.etlMemberRegtypeLog(ssc, sparkSession) //清洗用户注册数据
    EtlDataService.etlMemPayMoneyLog(ssc, sparkSession) //导入用户支付情况记录
    EtlDataService.etlMemVipLevelLog(ssc, sparkSession) //导入vip基础数据

  }

}
