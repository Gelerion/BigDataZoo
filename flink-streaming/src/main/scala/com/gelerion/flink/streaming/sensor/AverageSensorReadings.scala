package com.gelerion.flink.streaming.sensor

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.api.scala._

/**
  * Created by denis.shuvalov on 20/11/2017.
  */
object AverageSensorReadings {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    val sensorData: DataStream[SensorReading] = env
      // ingest sensor readings with a SensorSource SourceFunction
      .addSource(new SensorSource)/*.setParallelism(4)*/
      // assign timestamps and watermarks (required for event time)
      .assignTimestampsAndWatermarks(new SensorTimerAssigner)

    val avgTemp: DataStream[SensorReading] = sensorData
      //convert Fahrenheit to Celsius
      .map(new CelsiusConverter)
      // organize readings by sensor id
      .keyBy(_.id)
      // group readings in 5 second tumbling windows
      .timeWindow(Time.seconds(5))
      .apply(new TemperatureAverager)

    avgTemp.print()

    env.execute("Compute average sensor temperature")
  }
}
