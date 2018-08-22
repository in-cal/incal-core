organization := "org.incal"

name := "incal-core"

version := "0.0.4"

scalaVersion := "2.11.12"

resolvers ++= Seq(
  Resolver.mavenLocal
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.11.12",
  "commons-io" % "commons-io" % "2.6",
  "com.typesafe.akka" %% "akka-stream" % "2.4.9"
  //  "ch.qos.logback" % "logback-classic" % "1.2.2"
)