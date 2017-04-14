import org.apache.spark.SparkConf
import org.apache.spark.sql.{ColumnName, DataFrame, SaveMode, SparkSession}

import scala.util.control.NonFatal

object MainApp {

  val conf: SparkConf = new SparkConf()
      .setMaster("local")
      .setAppName("ETL-Challenge")

  val sparkSession = SparkSession.builder().config(conf).getOrCreate()

  // sbt "run mysql hdfs://localhost:9000 person 127.0.1.1 33060 hackathon root root"
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

  def preImportCSV(args: Array[String]) = {
    val outputLocation = args(1)
    val modelKey = args(2)
    val inputPath = args(3)
    importCSV(outputLocation, modelKey, inputPath)
  }

  def preImportJDBC(args: Array[String]) = {
    val outputLocation = args(1)
    val modelKey = args(2)
    val host = args(3)
    val port = args(4)
    val database = args(5)
    val user = args(6)
    val password = args(7)
    val url = s"jdbc:mysql://$host:$port/$database"
    importJDBC(outputLocation, modelKey, url, user, password)
  }

  def importCSV(outputLocation: String, modelKey: String, csvLocation: String) = {
    val dataFrame = sparkSession
        .read
        .option("header", "true")
        .csv(csvLocation)

    importDataFrame(outputLocation, modelKey, dataFrame)
  }

  def importJDBC(outputLocation: String, modelKey: String, jdbcUrl: String, user: String, password: String) = {
    val dataFrame = sparkSession
        .read
        .format("jdbc")
        .option("url", jdbcUrl)
        .option("user", user)
        .option("password", password)
        .option("dbtable", modelKey)
        .load()

    importDataFrame(outputLocation, modelKey, dataFrame)
  }

  def importDataFrame(outputLocation: String, modelKey: String, dataFrame: DataFrame) = {
    val existingDataFrame = readModelAsDataFrame(outputLocation, modelKey)

    val newDataFrame = existingDataFrame
        .map { processUpdates(outputLocation, modelKey, _, dataFrame) }
        .getOrElse(dataFrame)

    writeDataFrameToLocation(newDataFrame, modelLocation(outputLocation, modelKey))
  }

  def writeDataFrameToLocation(dataFrame: DataFrame, location: String) = {
    dataFrame.write
        .mode(SaveMode.Overwrite)
        .option("header", "true")
        .csv(location)
  }

  def processUpdates(outputLocation: String, modelKey: String, existingDataFrame: DataFrame, newDataFrame: DataFrame): DataFrame = {
    // In order to update the same csv, we need to backup it first in order to read and write simultaneously
    val backupLocation = modelLocation(outputLocation, modelKey) + "_bak"
    writeDataFrameToLocation(existingDataFrame, backupLocation)

    // now use the backup dataframe
    val backupDataFrame = readDataFrameFromLocation(backupLocation).get
    val modelId = backupDataFrame.columns.filter(_.toLowerCase == "id").head // assuming id column exists
    val columns = backupDataFrame
        .columns
        .filter(_ != modelId)
        .map { name => new ColumnName(s"new.$name") }
        .toList

    println(s"EXISTING COLUMNS = ${backupDataFrame.columns.mkString(",")}")
    backupDataFrame
        .as("old")
        .join(newDataFrame.as("new"), Seq(modelId), "outer")
        .select(new ColumnName(modelId) :: columns: _*)
  }

  def modelLocation(outputLocation: String, modelKey: String) = {
    s"$outputLocation/$modelKey"
  }

  def readModelAsDataFrame(outputLocation: String, modelKey: String): Option[DataFrame] = {
    readDataFrameFromLocation(modelLocation(outputLocation, modelKey))
  }

  def readDataFrameFromLocation(location: String): Option[DataFrame] = {
    try {
      val dataFrame = sparkSession
          .read
          .option("header", "true")
          .csv(location)

      println("DATAFRAME ALREADY EXISTS !!!")
      dataFrame.show(10)
      Some(dataFrame)
    } catch {
      case NonFatal(_) =>
        println("DATAFRAME DOESN'T EXISTS !!!")
        None
    }
  }
}
