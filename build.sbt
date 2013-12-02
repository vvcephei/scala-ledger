import AssemblyKeys._ // put this at the top of the file

name := "scala-ledger"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
    "org.apache.httpcomponents" % "httpclient" % "4.2.1",
    "com.sun.jersey" % "jersey-core" % "1.17.1",
    "com.sun.jersey" % "jersey-client" % "1.17.1",
    "com.sun.jersey" % "jersey-json" % "1.17.1",
    //"javax.ws.rs" % "jsr311-api" % "1.1.1",
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.2.3",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.2.3",
    "org.joda" % "joda-convert" % "1.2",
    "joda-time" % "joda-time" % "2.3",
    "com.github.tototoshi" %% "scala-csv" % "0.8.0",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.1.3",
    "com.beust" % "jcommander" % "1.30",
    "org.apache.opennlp" % "opennlp-tools" % "1.5.3",
    "org.apache.opennlp" % "opennlp-maxent" % "3.0.3"
)

libraryDependencies ++= Seq(
    "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
)

mainClass := Some("org.vvcephei.banketl.ETL")

net.virtualvoid.sbt.graph.Plugin.graphSettings

assemblySettings

mainClass in assembly := Some("org.vvcephei.banketl.ETL")

