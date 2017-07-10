object Main extends InitSpark {

  def main(args: Array[String]) : Unit = {
    import spark.implicits._

    val namesRdd = sc.parallelize(Seq("Denis", "Marina", "Lena"))

    namesRdd.filter(name => name.startsWith("D"))
      .collect()
      .foreach(println)

    close
  }
}
