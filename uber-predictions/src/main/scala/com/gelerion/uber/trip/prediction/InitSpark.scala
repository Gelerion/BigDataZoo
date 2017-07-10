package com.gelerion.uber.trip.prediction

import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}

trait InitSpark {
  val spark: SparkSession = SparkSession.builder()
    .appName("Uber trip prediction")
    .master("local[*]")
    .getOrCreate()

  val sc: SparkContext = spark.sparkContext
  val sqlContext: SQLContext = spark.sqlContext

  def close = {
    spark.close()
  }
}
