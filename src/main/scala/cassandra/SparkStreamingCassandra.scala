package cassandra

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

import com.datastax.spark.connector._
import org.apache.spark.sql.cassandra._

/*
  Exception in thread "main" java.lang.NoClassDefFoundError: org/apache/spark/streaming/StreamingContext
  Edit Run configuration -> Add dependencies with "provided" scope to classpath
 */

object SparkStreamingCassandra {

  def main(args: Array[String]): Unit = {

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "SparkStreamingKafka",
      "auto.offset.reset" -> "latest"
    )

    val topics = Array("java")
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreamingKafkaApp")
    val streamingContext = new StreamingContext(sparkConf, Seconds(1))

    val kafkaStream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    streamingContext.sparkContext.setLogLevel("ERROR")

    val result = kafkaStream.map(record => (record.key(), record.value()))

    result.foreachRDD(rdd => {
      val rddout = rdd.map(x => {
        val elements = x._2.split(",")
        val id = x._1
        val name = elements(0)
        val city = elements(1)
        val age = elements(2)

        (id, name, city, age)
      })

      rddout.saveToCassandra("spark", "user", SomeColumns("id", "name", "city", "age"))
    })

    streamingContext.start()
    streamingContext.awaitTermination()
  }

}