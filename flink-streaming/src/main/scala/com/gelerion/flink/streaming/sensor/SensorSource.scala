package com.gelerion.flink.streaming.sensor

import java.lang.System.currentTimeMillis

import org.apache.flink.streaming.api.functions.source.SourceFunction

import scala.util.Random

/**
  * Created by denis.shuvalov on 20/11/2017.
  */
class SensorSource extends SourceFunction[SensorReading] {
  @volatile var isRunning: Boolean = true


  override def run(ctx: SourceFunction.SourceContext[SensorReading]): Unit = {
    val rnd = new Random()

    while (isRunning) {
      Thread.sleep(50)
      val reading = SensorReading(s"sensor_${rnd.nextInt(12)}", currentTimeMillis(), 32 + rnd.nextInt(31))
      ctx.collect(reading)
    }

  }

  override def cancel(): Unit = {
    isRunning = false
  }

}
