name := """timesheets"""
organization := "com.codeflow"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies += "org.scalatestplus" %% "junit-4-13" % "3.2.2.0" % "test"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.codeflow.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.codeflow.binders._"
