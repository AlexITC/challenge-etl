import java.security.MessageDigest
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession, Row}
import org.apache.spark.sql.functions._

object MainApp {

  val conf: SparkConf = new SparkConf()
      .setMaster("local")
      .setAppName("ETL-Challenge")

  val sparkSession = SparkSession.builder().config(conf).getOrCreate()

  // sbt "run mysql person 127.0.1.1 33060 hackathon root root"
  def main(args: Array[String]): Unit = {
    // TODO: Parse arguments using scopt
    try {
      args(0) match {
        case "csv" => preImportCSV(args)
        case "mysql" => preImportJDBC(args)
        case opt => sys.error(s"Unknown option: $opt")
      }
    } finally {
      sparkSession.stop()
    }
  }

  def md5(row: Row) = {
    val digest = MessageDigest.getInstance("MD5")
    digest.digest(row.mkString("$").getBytes).map("%02x".format(_)).mkString
  }

  def preImportCSV(args: Array[String]) = {
    val modelKey = args(1)
    val inputPath = args(2)
    importCSV(modelKey, inputPath)
  }

  def preImportJDBC(args: Array[String]) = {
    val modelKey = args(1)
    val host = args(2)
    val port = args(3)
    val database = args(4)
    val user = args(5)
    val password = args(6)
    val url = s"jdbc:mysql://$host:$port/$database"
    importJDBC(modelKey, url, user, password)
  }

  // TODO: Read it from arguments
  val outputHost = "hdfs://localhost:9000"

  def importCSV(modelKey: String, csvLocation: String) = {
    val dataFrame = sparkSession
        .read
        .option("header", "true")
        .csv(csvLocation)

    importDataFrame(modelKey, dataFrame)
  }

  def importJDBC(modelKey: String, jdbcUrl: String, user: String, password: String) = {
    val dataFrame = sparkSession
        .read
        .format("jdbc")
        .option("url", jdbcUrl)
        .option("user", user)
        .option("password", password)
        .option("dbtable", modelKey)
        .load()

    importDataFrame(modelKey, dataFrame)
  }

  def importDataFrame(modelKey: String, dataFrame: DataFrame) = {
    val outputLocation = s"$outputHost/$modelKey"
    val hashFunc = udf(md5 _)
    val appendDF = dataFrame
        .withColumn("hash", hashFunc(struct(dataFrame.columns.map(dataFrame(_)) : _*)))
        .withColumn("insertionTime", current_timestamp())
    appendDF.write
        .format("csv")
        .option("header", "true")
        .mode("overwrite")
        .save(outputLocation)
  }
}
