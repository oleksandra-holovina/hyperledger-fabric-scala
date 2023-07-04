version := "0.1"
scalaVersion := "2.13.10"

val akkaVersion = "2.7.0"
val akkaHttpVersion = "10.4.0"
val loggerVersion = "1.7.25"
val hyperLedgerVersion = "2.4.1"

lazy val api = (project in file("modules/api"))
  .settings(
    name := "api",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "org.slf4j" % "slf4j-api" % loggerVersion,
      "org.slf4j" % "slf4j-simple" % loggerVersion,

      "org.hyperledger.fabric" % "fabric-gateway" % "1.1.1",
      "io.grpc" % "grpc-netty-shaded" % "1.50.1",
      "com.google.code.gson" % "gson" % "2.9.1",
    )
  )

lazy val contract = (project in file("modules/contracts"))
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "contract",
    dockerBaseImage := "openjdk:11-slim-buster",
    assembly / mainClass := Some("org.hyperledger.fabric.contract.ContractRouter"),
    assembly / assemblyJarName := "hlf.jar",
    assembly / assemblyMergeStrategy  := {
      case PathList("META-INF", _*) => MergeStrategy.discard
      case _ => MergeStrategy.first
    },
    resolvers ++= Seq(
      Resolver.mavenLocal,
      Resolver.mavenCentral,
      "jitpack" at "https://jitpack.io"
    ),
    libraryDependencies ++= Seq(
      "org.json" % "json" % "20220924",
      "com.owlike" % "genson" % "1.5",
      "org.hyperledger.fabric-chaincode-java" % "fabric-chaincode-shim" % hyperLedgerVersion,
      "org.slf4j" % "slf4j-api" % loggerVersion,
      "org.slf4j" % "slf4j-simple" % loggerVersion
    )
  )

lazy val root = (project in file("."))
  .settings(
    name := "hlf"
  )
  .aggregate(api, contract)
  .settings(
    update / aggregate := false
  )
