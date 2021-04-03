name := """timesheets"""
organization := "com.codeflow"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"
crossScalaVersions := List("2.13", "2.12")  // allow compilation of older libraries

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies += "org.scalatestplus" %% "junit-4-13" % "3.2.2.0" % "test"

// Database setup
// https://www.playframework.com/documentation/2.8.x/Developing-with-the-H2-Database
libraryDependencies ++= Seq(
  evolutions,
  jdbc,
  "com.h2database"           % "h2"         % "1.4.195",
  "org.playframework.anorm" %% "anorm"      % "2.6.8",
  "org.postgresql"           % "postgresql" % "42.2.18"
)

// Swagger
libraryDependencies += "com.github.dwickern" %% "swagger-play2.8" % "3.0.0"
libraryDependencies += "org.webjars"          % "swagger-ui"      % "3.43.0"

// ISO types for currencies
libraryDependencies += "com.vitorsvieira" % "scala-iso_2.12" % "0.1.2"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.codeflow.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.codeflow.binders._"
