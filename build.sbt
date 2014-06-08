name := "scala-ledger"

organization := "org.vvcephei"

version := "1.0"

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

net.virtualvoid.sbt.graph.Plugin.graphSettings

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/vvcephei/scala-ledger</url>
  <licenses>
    <license>
      <name>Apache</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:vvcephei/scala-ledger.git</url>
    <connection>scm:git:git@github.com:vvcephei/scala-ledger.git</connection>
  </scm>
  <developers>
    <developer>
      <id>vvcephei</id>
      <name>John Roesler</name>
      <url>http://www.vvcephei.org</url>
    </developer>
  </developers>)


