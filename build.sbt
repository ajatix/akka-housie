import Dependencies._

name := "akka-housie"

organization := "io.github.ajatix"

version := "0.1"

scalaVersion := "2.11.8"

shellPrompt := {state =>
  s"[${name.value}] "
}

libraryDependencies ++= Seq(
  scalatest % Test
) ++ akkaDependencies ++ akkaStreamDependencies ++ akkaHttpDependencies ++ akkaPersistenceDependencies

