import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object MainApp {

  private def checkInFilePath: String = {
    val resource = this.getClass.getClassLoader.getResource("csv/check-in.csv")
    if (resource == null) {
      sys.error("The input file was not found")
    } else {
      resource.toURI.getPath
    }
  }

  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf()
        .setMaster("local")
        .setAppName("ETL-Challenge")

    val sparkSession = SparkSession.builder().config(conf).getOrCreate()

    try {
      println("Reading DataFrame")
      val dataFrame = sparkSession
            .read
            .option("header", "true")
            .csv(checkInFilePath)

      println("Saving DataFrame")
      dataFrame.write
          .format("csv")
          .option("header", "true")
          .save("output_csv")

    } finally {
      sparkSession.stop()
    }
  }
}