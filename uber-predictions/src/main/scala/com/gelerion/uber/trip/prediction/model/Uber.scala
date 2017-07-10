package com.gelerion.uber.trip.prediction.model

case class Uber(dt: String, lat: Double, lon: Double, base: String) extends Serializable

object Uber {
  def parseUber(str: String): Uber = {
    val p = str.split(",")
    Uber(p(0), p(1).toDouble, p(2).toDouble, p(3))
  }
}

