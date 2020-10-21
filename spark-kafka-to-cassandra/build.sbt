lazy val root = (project in file(".")).
  settings(
    name := "spark-kafka-to-cassandra",
    version := "1.0",
    scalaVersion := "2.12.0",
    mainClass in Compile := Some("com.abdelhadi.CsvToKafka"),
    mainClass in assembly := Some("com.abdelhadi.CsvToKafka"),
  )

scalaVersion := "2.12.10"
val sparkVersion = "3.0.1"

libraryDependencies += "org.apache.spark" %% "spark-core" % "3.0.1"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.0.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.0.1"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.0.1"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.6.0"
libraryDependencies += "com.github.jnr" % "jnr-posix" % "3.1.2"
libraryDependencies += "joda-time" % "joda-time" % "2.10.6"
libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" % "3.0.0"
libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector-assembly" % "3.0.0"

// META-INF discarding
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}