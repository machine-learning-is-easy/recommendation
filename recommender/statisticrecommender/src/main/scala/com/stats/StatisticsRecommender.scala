package com.stats
import java.text.SimpleDateFormat
import java.util.Date
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

case class Rating(UserId: Int, ProductId: Int, rating: Double, Timestamp: Int)
case class MongoConfig(uri: String, db: String)

object StatisticsRecommender {
  val MONGODB_RATING_COLLECTION = "Rating"
  val RATE_MORE_PRODUCTS = "RateMoreProducts"
  val RATE_MORE_RECENTLY_PRODUCTS = "RateMoreRecentlyProducts"
  val AVERAGE_PRODUCTS = "AverageProducts"

  def main(args: Array[String]): Unit = {
    val config = Map(
      "spark.cores" -> "local[1]",
      "mongo.uri" -> "mongodb://localhost:27017/recommender",
      "mongo.db" -> "recommender"
    )
    val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("StatisticsRecommender")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._
    implicit val mongoConfig = MongoConfig( config("mongo.uri"), config("mongo.db") )

    val ratingDF = spark.read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_RATING_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[Rating]
      .toDF()

    ratingDF.createOrReplaceTempView("ratings")

    val rateMoreProductsDF = spark.sql("select productId, count(productId) as count from ratings group by productId order by count desc")
    storeDFInMongoDB( rateMoreProductsDF, RATE_MORE_PRODUCTS )

    val simpleDateFormat = new SimpleDateFormat("yyyyMM")

    spark.udf.register("changeDate", (x: Int)=>simpleDateFormat.format(new Date(x * 1000L)).toInt)
    val ratingOfYearMonthDF = spark.sql("select productId, score, changeDate(timestamp) as yearmonth from ratings")
    ratingOfYearMonthDF.createOrReplaceTempView("ratingOfMonth")
    val rateMoreRecentlyProductsDF = spark.sql("select productId, count(productId) as count, yearmonth from ratingOfMonth group by yearmonth, productId order by yearmonth desc, count desc")
    storeDFInMongoDB( rateMoreRecentlyProductsDF, RATE_MORE_RECENTLY_PRODUCTS )

    val averageProductsDF = spark.sql("select productId, avg(score) as avg from ratings group by productId order by avg desc")
    storeDFInMongoDB( averageProductsDF, AVERAGE_PRODUCTS )

    spark.stop()
  }
  def storeDFInMongoDB(df: DataFrame, collection_name: String)(implicit mongoConfig: MongoConfig): Unit ={
    df.write
      .option("uri", mongoConfig.uri)
      .option("collection", collection_name)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()
  }
}
