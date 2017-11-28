package com.gelerion.flink.streaming.connect

import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.util.Collector

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by denis.shuvalov on 22/11/2017.
  */
object OffersProposal {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // the stream of indicators contains factor-value pairs
    val factors: DataStream[(String, Double)] = env.fromElements(("a", 1.2), ("a", 0.9))

    // the stream of price requests contains items
    val priceRequests: DataStream[Item] = env.fromElements(Item("a", 15, 10))


    val broadcast: DataStream[(String, Double)] = factors.broadcast

    // broadcast factors to all operator instances
    //    priceRequests.connect(factors.broadcast)
/*    val offers: DataStream[Offer] = priceRequests.connect(factors)
      .keyBy(0, 0)
      .flatMap(new CoFlatMapFunction[Item, (String, Double), Offer] {

        // shared state between the two  NOTE: state is not checkpointed
        val factorValues: mutable.HashMap[String, Double] = mutable.HashMap.empty

        override def flatMap1(item: Item, out: Collector[Offer]) = {
          out.collect(computePrice(item))
        }

        override def flatMap2(value: (String, Double), out: Collector[Offer]) = {
          val factor = value._1
          factorValues.put(factor, value._2)
        }

        // compute prices for items using factorValues
        def computePrice(item: Item) = {
          Offer("regular", item.price * factorValues.getOrElse(item.name, 1.0))
        }
      })

    offers.print()*/

    val offers: DataStream[Offer] = factors.connect(priceRequests)
      .keyBy(0, 0)
      .flatMap(new CoFlatMapFunction[(String, Double), Item, Offer] {

        // shared state between the two  NOTE: state is not checkpointed
        val factorValues: ListBuffer[Double] = new ListBuffer[Double]

        override def flatMap1(value: (String, Double), out: Collector[Offer]) = {
          //val factor = value._1
          factorValues += value._2
        }

        override def flatMap2(item: Item, out: Collector[Offer]) = {
          out.collect(computePrice(item))
        }

        // compute prices for items using factorValues
        def computePrice(item: Item) = {
          var price = item.price
          for (factor <- factorValues) price *= factor
          Offer("regular", price)
        }
      })

    offers.print()

    env.execute("Offers proposal")
  }

}
