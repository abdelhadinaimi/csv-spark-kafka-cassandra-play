package com.abdelhadi

import java.util.Properties
import java.util.concurrent.Future

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer

import scala.util.{Failure, Success, Try}

class Producer {
  val brokers = "rocket-01.srvs.cloudkafka.com:9094,rocket-02.srvs.cloudkafka.com:9094,rocket-03.srvs.cloudkafka.com:9094"
  val username = "9iirknia"
  val password = "RcPU23DrwjfpZOYAfMLkLanPfJuXkcJW"

  val jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";"
  val jaasCfg: String = String.format(jaasTemplate, username, password)

  val topic: String = username + "-person"

  private val props: Properties = new Properties()
  props.put("bootstrap.servers" , brokers)
  props.put("key.serializer" , classOf[StringSerializer])
  props.put("value.serializer" , classOf[StringSerializer])
  props.put("group.id" , (username + "-consumer"))
  props.put("auto.offset.reset" , "latest")
  props.put("enable.auto.commit" , (true: java.lang.Boolean))
  props.put("security.protocol" , "SASL_SSL")
  props.put("sasl.mechanism", "SCRAM-SHA-256")
  props.put("sasl.jaas.config", jaasCfg)

  private val producer = new KafkaProducer[String, String](props)
  private var n = 0
  def sendMessage(message: String): Try[Future[RecordMetadata]] = {
    try {
      val record = new ProducerRecord[String, String](topic, message.hashCode.toString, message)
      n = n + 1
      Success(producer.send(record))
    } catch {
      case e: Exception => Failure(e)
    }
  }

  def close(): Unit = {
    println(s"Processed a total of $n messages")
    producer.close()
  }
}
