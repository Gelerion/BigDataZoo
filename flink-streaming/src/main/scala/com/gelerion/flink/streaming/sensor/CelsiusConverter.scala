package com.gelerion.flink.streaming.sensor

import org.apache.flink.api.common.functions.MapFunction

/**
  * Created by denis.shuvalov on 21/11/2017.
  */
class CelsiusConverter extends MapFunction[SensorReading, SensorReading] {

  override def map(r: SensorReading) : SensorReading = {
    val celsius = (r.temperature - 32) * (5.0 / 9.0)
    SensorReading(r.id, r.timestamp, Math.floor(celsius))
  }
}
