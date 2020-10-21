package com.abdelhadi

import org.apache.spark.SparkConf
import org.apache.spark.streaming._

object CsvToKafka {
  val delimiter = ","
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
      .setMaster("spark://abdelhadi:7077")
      .set("spark.executor.memory", "480M")
      .set("spark.shuffle.service.enabled", "true")
      .set("spark.dynamicAllocation.enabled", "true")
      .set("spark.executor.cores", "2")
      .set("spark.dynamicAllocation.maxExecutors","1") // num-executors
      .setJars(Seq("target/scala-2.12/spark-to-kafka-assembly-1.0.jar"))
      .setAppName("csv-to-kafka")

    val ssc = new StreamingContext(sparkConf, Seconds(10))
    val csvs = ssc.textFileStream("file:///home/abdelhadi/test")

    csvs
      .mapPartitions(partition => {
        val producer = new Producer()
        partition.foreach(record => {
          val splitLine = record.split(delimiter)
          splitLine(0) = "k_"+splitLine(0)
          val newLine = splitLine.slice(0, 5).mkString(delimiter)
          producer.sendMessage(newLine)
        })
        producer.close()
        partition
      }).count().print()

    ssc.start()
    ssc.awaitTermination()
  }
}
