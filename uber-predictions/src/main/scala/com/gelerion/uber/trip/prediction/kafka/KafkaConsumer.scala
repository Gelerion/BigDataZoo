package com.gelerion.uber.trip.prediction.kafka

import com.gelerion.uber.trip.prediction.InitSpark
import org.apache.spark.ml.clustering.KMeansModel

//https://dzone.com/articles/end-to-end-application-for-monitoring-real-time-ub
//https://github.com/caroljmcdonald/mapr-sparkml-streaming-uber/tree/cc6492625be75796304fe6c04c0c4beb947f3ac4/src/main/scala/com
object KafkaConsumer extends InitSpark {

  def main(args: Array[String]): Unit = {
    val model = KMeansModel.load("/home/denis-shuvalov/Documents/Other/UberTrip/model")
    model.clusterCenters.foreach(println)
  }
}
