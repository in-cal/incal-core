import com.typesafe.sbt.license.{DepModuleInfo, LicenseInfo}

organization := "org.in-cal"

name := "incal-core"

version := "0.2.2.RC.1"

description := "Core library for In-Cal projects containing utility classes, repo interfaces, and shared/common models."

isSnapshot := false

scalaVersion := "2.11.12"

resolvers ++= Seq(
  Resolver.mavenLocal
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.11.12",
  "commons-io" % "commons-io" % "2.6",
  "com.typesafe.akka" %% "akka-stream" % "2.4.17"
)

// some of the libs' licenses are not included hence we need to provide them (override) manually
licenseOverrides := {
  case DepModuleInfo("commons-io", "commons-io", _) =>
    LicenseInfo(LicenseCategory.Apache, "Apache License v2.0", "http://www.apache.org/licenses/LICENSE-2.0")
}

// POM settings for Sonatype
homepage := Some(url("https://in-cal.org"))

publishMavenStyle := true

scmInfo := Some(ScmInfo(url("https://github.com/in-cal/incal-core"), "scm:git@github.com:in-cal/incal-core.git"))

developers := List(Developer("bnd", "Peter Banda", "peter.banda@protonmail.com", url("https://peterbanda.net")))

licenses += "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")

publishMavenStyle := true

// publishTo := sonatypePublishTo.value

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)
