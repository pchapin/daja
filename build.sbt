import Dependencies._

enablePlugins(Antlr4Plugin)

ThisBuild / organization  := "edu.vtc"
ThisBuild / version       := "0.1.0-SNAPSHOT"
//ThisBuild / scalaVersion  := "2.13.12"
ThisBuild / scalaVersion  := "3.3.1"
ThisBuild / scalacOptions :=
  Seq("-encoding", "UTF-8", // Encoding of the source files.
      "-feature",
      "-deprecation",       // Tell us about deprecated things.
      "-unchecked",
      "-Wunused:nowarn")    // Warn if the nowarn annotation doesn't actually suppress a warning.

Test / logBuffered := false

// The Graph-for-Scala library for Scala 2.13 is "3.x compatible." However, at this time
// (December 2023) there is no version in Maven explicitly marked for Scala 3. Thus, I am
// manually providing that library here because otherwise SBT fails when it can't download
// a jar for Scala 3. Also see the comments in project/Dependencies.scala.
//
lazy val daja = (project in file("."))
  .settings(
    name := "Daja",
    libraryDependencies ++= dajaDeps,
    Test / unmanagedJars += file("lib/graph-core_2.13-1.13.6.jar"),

    Antlr4 / antlr4Version     := "4.13.1",
    Antlr4 / antlr4PackageName := Some("edu.vermontstate.daja"),
    Antlr4 / antlr4GenListener := true,
    Antlr4 / antlr4GenVisitor  := true
  )
  .dependsOn(dragon)


lazy val tiger = (project in file("Tiger"))
  .settings(
    name := "Tiger",
    libraryDependencies ++= tigerDeps
  )
  .dependsOn(dragon)


lazy val dragon = (project in file("Dragon"))
  .settings(
    name := "Dragon",
    libraryDependencies ++= dragonDeps
  )
