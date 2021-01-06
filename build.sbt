name := """timesheets"""
organization := "com.codeflow"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies += "org.scalatestplus" %% "junit-4-13" % "3.2.2.0" % "test"

// Database setup
// https://www.playframework.com/documentation/2.8.x/Developing-with-the-H2-Database
libraryDependencies ++= Seq(
  evolutions,
  jdbc,
  "com.h2database" % "h2" % "1.4.195",
  "org.playframework.anorm" %% "anorm" % "2.6.8",
  "org.postgresql" % "postgresql" % "42.2.18"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.codeflow.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.codeflow.binders._"
