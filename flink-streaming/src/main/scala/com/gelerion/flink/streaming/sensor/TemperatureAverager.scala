package com.gelerion.flink.streaming.sensor

import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

/**
  * Created by denis.shuvalov on 20/11/2017.
  */
class TemperatureAverager extends WindowFunction[SensorReading, SensorReading, String, TimeWindow] {

  override def apply(key: String, window: TimeWindow, input: Iterable[SensorReading], out: Collector[SensorReading]): Unit = {
    out.collect(SensorReading(key, System.currentTimeMillis(), input.map(_.temperature).sum / input.size))
  }

}
