import sbt._

import Versions._

object Dependencies {

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val akkaRemote = "com.typesafe.akka" %% "akka-remote" % akkaVersion
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion
  val akkaClusterSharding = "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
  val akkaPersistence = "com.typesafe.akka" %% "akka-persistence" % akkaVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  val akkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion

  // loggers
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.1.3"

  // test frameworks
  val scalatest = "org.scalatest" %% "scalatest" % scalatestVersion
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  val akkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
  val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion

  // optional
  val typesafeConfig = "com.typesafe" % "config" % "1.3.1"

  // required for akka persistence
  val leveldb = "org.iq80.leveldb" % "leveldb" % "0.7"
  val leveldbjniAll = "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"

  val akkaDependencies = Seq(akkaActor, akkaTestkit % Test)
  val akkaStreamDependencies = Seq(akkaStream, akkaStreamTestkit % Test)
  val akkaPersistenceDependencies = Seq(akkaPersistence, leveldb, leveldbjniAll)
  val akkaHttpDependencies = Seq(akkaHttp, akkaHttpSprayJson, akkaHttpTestkit % Test)
  val akkaClusterDependencies = Seq(akkaCluster, akkaClusterSharding)
}
