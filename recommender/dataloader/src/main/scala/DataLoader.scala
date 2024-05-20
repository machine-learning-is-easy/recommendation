package com.recommender

import com.mongodb.casbah.MongoClient
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

case class Product(productId: Int, name: String, ImageUrl: String, category: String, tags:String)

case class Rating(UserId: Int, ProductId: Int, rating: Double, Timestamp: Int)

case class MongoConfig(uri: String, db: String)
object DataLoader {
  val PRODUCT_DATA_PATH = "/Volumes/ExtremeSSD/java/ECommerceRecommendSystem/recommender/DataLoader/src/main/resources/products.csv"
  val RATING_DATA_PATH = "/Volumes/ExtremeSSD/java/ECommerceRecommendSystem/recommender/DataLoader/src/main/resources/ratings.csv"
  val MONGODB_PRODUCT_COLLECTION = null
  val MONGODB_RATING_COLLECTION = null

  def main(array: Array[String]): Unit = {

    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://localhost:27017/recommender",
      "mongo.db" -> "recommender"
    )
    val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("DataLoader")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._

    val productRDD = spark.sparkContext.textFile(PRODUCT_DATA_PATH)
    val productDF = productRDD.map(item => {
      val attr = item.split("\\^")
      Product(attr(0).toInt, attr(1).trim, attr(4).trim, attr(5).trim, attr(6).trim)}).toDF()

    val ratingRDD = spark.sparkContext.textFile(RATING_DATA_PATH)
    val ratingDF = ratingRDD.map(item => {
      val attr = item.split(",")
      Rating(attr(0).toInt,
        attr(1).toInt,
        attr(2).toDouble,
        attr(3).toInt: Int)
    }).toDF()

    implicit val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))
    FlushData2MongoDB(productDF, ratingDF)
  }
  def FlushData2MongoDB(productDF: DataFrame, ratingDF: DataFrame)(implicit mongoConfig: MongoConfig): Unit = {
    val mongoClient = MongoClient( MongoClientURI(mongoConfig) )
    val productCollection = mongoClient(mongoConfig.db)(MONGODB_PRODUCT_COLLECTION)
    val ratingCollection = mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION)

    productCollection.dropCollection()
    ratingCollection.dropCollection()

    productDF.write
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_PRODUCT_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    ratingDF.write
      .option ("uri", mongoConfig.uri)
      .option("collection", MONGODB_RATING_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    productCollection.createIndex(MongoDBObject("productId" -> 1))
    ratingCollection.createIndex(MongoDBObject("productId" -> 1))
    ratingCollection.createIndex(MongoDBObject("userId" -> 1))

    mongoClient.close()
  }

}
