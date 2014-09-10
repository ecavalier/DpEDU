import play.Project._

name := "DpEDU"

version := "1.0"

playScalaSettings

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "com.scalableminds" %% "play-mongev" % "0.2.8",
  "com.novus" %% "salat" % "1.9.5"
)