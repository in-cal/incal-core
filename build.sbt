import com.typesafe.sbt.license.{DepModuleInfo, LicenseInfo}

organization := "org.in-cal"

name := "incal-core"

version := "0.3.0-SNAPSHOT"

description := "Core library for In-Cal projects containing utility classes, repo interfaces, and shared/common models."

isSnapshot := false

scalaVersion := "2.11.12" // "2.12.10"

resolvers ++= Seq(
  Resolver.mavenLocal
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "commons-io" % "commons-io" % "2.6",
  "commons-lang" % "commons-lang" % "2.6",
  "com.typesafe.akka" %% "akka-stream" % "2.4.17"
)

// some of the libs' licenses are not included hence we need to provide them (override) manually
licenseOverrides := {
  case DepModuleInfo("commons-io", "commons-io", _) =>
    LicenseInfo(LicenseCategory.Apache, "Apache License v2.0", "http://www.apache.org/licenses/LICENSE-2.0")
}

// POM settings for Sonatype
publishMavenStyle := true

scmInfo := Some(ScmInfo(url("https://github.com/in-cal/incal-core"), "scm:git@github.com:in-cal/incal-core.git"))

developers := List(
  Developer("bnd", "Peter Banda", "peter.banda@protonmail.com", url("https://peterbanda.net")),
  Developer("sherzinger", "Sascha Herzinger", "sascha.herzinger@uni.lu", url("https://wwwfr.uni.lu/lcsb/people/sascha_herzinger"))
)

licenses += "Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")

publishMavenStyle := true

// publishTo := sonatypePublishTo.value

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)
