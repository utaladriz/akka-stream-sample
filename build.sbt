name := "wom"
organization := "cl.exe"
version := "1.0"

scalaVersion := "2.11.7"
val akkaVersion = "2.3.12"
val camelVersion = "2.15.3"
val slf4jVersion ="1.7.5"

libraryDependencies ++= Seq (
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0",
  "com.typesafe.akka" % "akka-camel_2.11" % akkaVersion,
  "org.apache.camel" % "camel-ftp" % camelVersion,
  "org.apache.camel" % "camel-scala" % camelVersion,
  "org.slf4j" % "slf4j-simple" % slf4jVersion

)
    