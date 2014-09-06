import play.Project._

name := "DpEDU"

version := "1.0"

playScalaSettings

libraryDependencies ++= Seq( javaCore, javaJdbc, javaEbean, "mysql" % "mysql-connector-java" % "5.1.32")