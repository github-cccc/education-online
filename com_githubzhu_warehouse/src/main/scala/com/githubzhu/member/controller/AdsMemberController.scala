package com.githubzhu.member.controller

import com.githubzhu.member.service.AdsMemberService
import com.githubzhu.util.HiveUtil
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @Author: github_zhu
 * @Describtion:
 * @Date:Created in 2020/6/19 16:04
 * @ModifiedBy:
 *
 */
object AdsMemberController {

  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME", "root")
    //    System.setProperty("HADOOP_USER_NAME", "atguigu")
    val sparkConf = new SparkConf().setAppName("ads_member_controller").setMaster("local[*]")
    val sparkSession = SparkSession.builder().config(sparkConf).enableHiveSupport().getOrCreate()
    val ssc = sparkSession.sparkContext
    ssc.hadoopConfiguration.set("fs.defaultFS", "hdfs://mycluster")
    ssc.hadoopConfiguration.set("dfs.nameservices", "mycluster")
    HiveUtil.openDynamicPartition(sparkSession) //开启动态分区
    AdsMemberService.queryDetailApi(sparkSession, "20190722")
    //AdsMemberService.queryDetailSql(sparkSession, "20190722")
  }

}
