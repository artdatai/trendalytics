package com.trendalytics

import java.io._
import org.apache.http.client._
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json4s._
import org.json4s.native.JsonMethods._
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import java.net.URI
// import org.apache.hadoop.util.Progressable
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SQLContext._
// import org.apache.spark.sql.SQLContext.implicits._

import org.apache.spark.mllib.clustering.KMeans

// Import Row.
import org.apache.spark.sql.Row;

// Import Spark SQL data types
import org.apache.spark.sql.types.{StructType,StructField,StringType};



/**
 * @author ${user.name}
 */
object App {
  
  case class TweetRecord(key_name: String, text: String, id: String, username: String, retweets: Int, num_friends: Int, datetime: String)

  def getListOfFiles(dir: String):List[File] = {
      val d = new File(dir)
      if (d.exists && d.isDirectory) {
        d.listFiles.filter(_.isFile).toList
      } else {
        List[File]()
      }
  }

  def main(args : Array[String]) {

    val sc = new SparkContext(new SparkConf().setAppName("Trendalytics"))

    // val twitter = new TwitterFilter()
    // twitter.fetch()

    // val facebook = new FacebookStreamer()
    // facebook.fetch()

    // val tmdb = new TMDBStreamer()
    // tmdb.fetch()

    // val yelp = new YelpStreamer()
    // yelp.fetch()

    val hdfsObj = new HDFSManager()

    hdfsObj.createFolder("trendalytics_data")
    hdfsObj.createFolder("trendalytics_data/tweets")
    hdfsObj.createFolder("trendalytics_data/facebook_posts")
    // hdfsObj.createFolder("trendalytics_data/tweets_processed")

    val stopWordsFile = "trendalytics_data/stop_words.txt"

    if(!hdfsObj.isFilePresent(stopWordsFile))
        hdfsObj.saveFile(stopWordsFile)

    val yelpOutputFile = "trendalytics_data/output.csv"

    if(!hdfsObj.isFilePresent(yelpOutputFile))
        hdfsObj.saveFile(yelpOutputFile)


    println("####### Writing Tweet files to HDFS ########")
    val tweet_files = getListOfFiles("trendalytics_data/tweets")

    for (tweet_file <- tweet_files) {
        if(!hdfsObj.isFilePresent(tweet_file.toString()))
            hdfsObj.saveFile(tweet_file.toString())
    }

     println("####### Writing FB files to HDFS ########")
    
    val fb_files = getListOfFiles("trendalytics_data/facebook_posts")

    for (fb_file <- fb_files) {
        if(!hdfsObj.isFilePresent(fb_file.toString()))
            hdfsObj.saveFile(fb_file.toString())
    }


    /*val tweetFile = "trendalytics_data/tweets/test.txt"
    val tweets = sc.textFile(tweetFile)

    // val tweets = sc.textFile(tweet_files(0).toString())

    for (tweet <- tweets.take(5)) {
      println(tweet.split("\t").foreach(println))
    }

    val sqlContext = new SQLContext(sc)

    import sqlContext.implicits._

    val customSchemaTweet = StructType(Array(
        StructField("key", StringType, true),
        StructField("text", StringType, true),
        StructField("id", StringType, true),
        StructField("username", StringType, true),
        StructField("retweets", StringType, true),
        StructField("num_friends", StringType, true),
        StructField("datetime", StringType, true)))

    val df = sqlContext.read
        .format("com.databricks.spark.csv")
        .option("header", "false") // Use first line of all files as header
        .option("delimiter", "\t")
        .schema(customSchemaTweet)
        .load(tweetFile)

    println("Starting CSV processing...")
    df.printSchema()

    df.registerTempTable("tweets")

    val selectedData = df.select("key", "text")

    df.select(df("key"), df("text")).show()
*/
   val sqlContext = new SQLContext(sc)

    import sqlContext.implicits._


    for (fb_file <- fb_files) {

    //val fbFile = "trendalytics_data/facebook_posts/12042016_01.txt"
    //val post_file = sc.textFile(fb_File)

    // val tweets = sc.textFile(tweet_files(0).toString())

   

   


    val customSchemafb = StructType(Array(
        StructField("key", StringType, true),
        StructField("text", StringType, true),
        StructField("id", StringType, true),
        StructField("time", StringType, true)))

        val dfb = sqlContext.read
        .format("com.databricks.spark.csv")
        .option("header", "false") // Use first line of all files as header
        .option("delimiter", "\t")
        .schema(customSchemafb)
        .load(fb_file.toString())


    println("Starting CSV processing...")
    dfb.printSchema()

    dfb.registerTempTable("posts")

    val selectedData = dfb.select("key", "text")

    dfb.select(dfb("key"), dfb("text")).show()

  }
    return

  }
}
