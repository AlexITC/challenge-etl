name := "challenge-etl"
organization := "AlexITC"
version := "0.0.1"
scalaVersion := "2.11.8"

parallelExecution in Test := false
fork in Test := true

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-Xfuture",
  "-Xlint:missing-interpolator",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused",
  "-Ywarn-unused-import"
)


lazy val versions = new {
  val spark = "2.1.0"
  val mysql = "5.1.41"
}

libraryDependencies += "org.apache.spark" %% "spark-core" % versions.spark
libraryDependencies += "org.apache.spark" %% "spark-sql" % versions.spark
libraryDependencies += "mysql" % "mysql-connector-java" % versions.mysql
