package com.abdelhadi

import com.datastax.spark.connector.SomeColumns
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import com.datastax.spark.connector.streaming._

object KafkaToCassandra {
  val brokers = "rocket-01.srvs.cloudkafka.com:9094,rocket-02.srvs.cloudkafka.com:9094,rocket-03.srvs.cloudkafka.com:9094"
  val username = "9iirknia"
  val password = "RcPU23DrwjfpZOYAfMLkLanPfJuXkcJW"

  val jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";"
  val jaasCfg: String = String.format(jaasTemplate, username, password)

  val kafkaParams: Map[String, Object] = Map[String, Object](
    "bootstrap.servers" -> brokers,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> (username + "-consumer"),
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (true: java.lang.Boolean),
    "security.protocol" -> "SASL_SSL",
    "sasl.mechanism"-> "SCRAM-SHA-256",
    "sasl.jaas.config"-> jaasCfg
  )
  val topics = List(username + "-person")
  val delimiter = ","

  val csbPath = "file:///home/abdelhadi/projects/spark-kafka-to-cassandra/secure-connect.zip"
  val cassandraUser = "abdelhadi"
  val cassandraPass = "AsSUPTrFYYSJzX4"
  val keyspace = "person"
  val table = "person_by_email"

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
      .setMaster("spark://abdelhadi:7077")
      .set("spark.executor.memory", "480M")
      .set("spark.shuffle.service.enabled", "true")
      .set("spark.dynamicAllocation.enabled", "true")
      .set("spark.executor.cores", "2")
      .set("spark.dynamicAllocation.maxExecutors","1") // num-executors
      .setJars(Seq("target/scala-2.12/spark-kafka-to-cassandra-assembly-1.0.jar"))
      .setAppName("kafka-to-cassandra")
      .set("spark.cassandra.connection.config.cloud.path", csbPath)
      .set("spark.cassandra.auth.username", cassandraUser)
      .set("spark.cassandra.auth.password", cassandraPass)

    val ssc = new StreamingContext(sparkConf, Seconds(10))
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    stream
      .map(_.value().split(","))
      .filter(_.length == 5)
      .map {
        case Array(_, first_name, last_name, email, gender) => (email, first_name, last_name, gender)
      }
      .saveToCassandra(keyspace, table, SomeColumns("email", "first_name", "last_name", "gender"))
    ssc.start()
    ssc.awaitTermination()

  }
}
