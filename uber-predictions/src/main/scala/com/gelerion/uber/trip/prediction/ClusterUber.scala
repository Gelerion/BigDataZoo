package com.gelerion.uber.trip.prediction

import com.gelerion.uber.trip.prediction.model.Uber
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.functions.unix_timestamp
import org.apache.spark.sql.types._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

//https://dzone.com/articles/monitoring-real-time-uber-data-using-apache-apis-p
//https://github.com/caroljmcdonald/spark-ml-kmeans-uber/blob/master/src/main/scala/com/sparkml/uber/ClusterUber.scala
object ClusterUber extends App with InitSpark {

  import spark.implicits._

  val schema = StructType(Array(
    //StructField("dt", DataTypes.TimestampType, nullable = true),
    StructField("dt", DataTypes.StringType, nullable = true),
    StructField("lat", DataTypes.DoubleType, nullable = true),
    StructField("lon", DataTypes.DoubleType, nullable = true),
    StructField("base", DataTypes.StringType, nullable = true)
  ))

  //specifying the schema when loading data into a DataFrame will give better performance
  val ds: Dataset[Uber] = spark.read
    .option("header", true)
    .option("inferSchema", false)
    //.option("timestampFormat", "dd/MM/yyyy hh:mm:ss")
    .schema(schema)
    .csv("/home/denis-shuvalov/Documents/Other/UberTrip/uber-raw-data-sep14.csv")
    .as[Uber] //from implicit conversions

  ds.cache()
  //ds.printSchema()
  //ds.show() //2014-01-09 00:01:...|40.2201|-74.0021|B02512

  //define the feature columns to put in the feature vector
  val featureCols = Array("lat", "lon")
  //set the input column names
  val assembler = new VectorAssembler().setInputCols(featureCols).setOutputCol("features")
  //return a dataset with all of the feature columns in a vector column
  val featuredDs = assembler.transform(ds)
  //featuredDs.show() //2014-01-09 00:01:...|40.2201|-74.0021|B02512|[40.2201,-74.0021]

  val Array(trainingData, testData) = featuredDs.randomSplit(Array(0.7, 0.3), 5043)

  val kMeans = new KMeans().setK(8).setFeaturesCol("features").setPredictionCol("prediction")
  val model = kMeans.fit(trainingData)
  println("Final Centers: ")
  model.clusterCenters.foreach(println)

  val categories = model.transform(testData)

  categories.show
  categories.createOrReplaceTempView("uber")

  println("Which hours of the day and which cluster had the highest number of pickups?")
  categories.select(hour(unix_timestamp($"dt").cast("timestamp")).alias("hour"), $"prediction")
    .groupBy("hour", "prediction").agg(count("prediction").alias("count"))
    .orderBy(desc("count"))
    .show()

  println("How many pickups occurred in each cluster?")
  categories.groupBy("prediction")
    .count()
    .show()

//  model.write
//    .save("/home/denis-shuvalov/Documents/Other/UberTrip/model")
}