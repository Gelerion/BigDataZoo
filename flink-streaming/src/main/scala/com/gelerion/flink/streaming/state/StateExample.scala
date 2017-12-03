package com.gelerion.flink.streaming.state

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

/**
  * Created by denis.shuvalov on 29/11/2017.
  */
object StateExample {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val stream: DataStream[(Int, String)] = env.fromElements((1, "v"))

    stream.keyBy(0)
      .mapWithState((in: (Int, String), sum: Option[Int]) =>
        sum match {
          case Some(s) =>
            //update state with new sum
            ((in._2, s + in._1), Some(s + in._1))
          case None =>
            //initialize state with first value
            (in, Some(in._1))
        }
      )

  }

}
