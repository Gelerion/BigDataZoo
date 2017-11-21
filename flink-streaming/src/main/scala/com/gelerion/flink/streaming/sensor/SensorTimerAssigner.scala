package com.gelerion.flink.streaming.sensor

import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.windowing.time.Time

/**
  * Created by denis.shuvalov on 20/11/2017.
  */
class SensorTimerAssigner extends BoundedOutOfOrdernessTimestampExtractor[SensorReading](Time.seconds(6)) {

  override def extractTimestamp(element: SensorReading): Long = {
    element.timestamp
  }

}
