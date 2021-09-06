name := "hcluster-scala"

version := "0.1"

scalaVersion := "3.0.2"

idePackagePrefix := Some("net.xrrocha.hcluster")

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "ch.qos.logback" % "logback-classic" % "1.2.5",
  "org.scalatest" %% "scalatest" % "3.2.9" % Test,
  "org.apache.lucene" % "lucene-spellchecker" % "3.6.2" % Test
)
