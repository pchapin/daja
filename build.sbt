import Dependencies._

enablePlugins(Antlr4Plugin)

ThisBuild / organization  := "edu.vermontstate"
ThisBuild / version       := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion  := "3.3.4"  // Intended to be the latest LTS version.
ThisBuild / scalacOptions :=
  Seq("-encoding", "UTF-8", // Encoding of the source files.
      "-feature",
      "-deprecation",       // Tell us about deprecated things.
      "-unchecked")

Test / logBuffered := false

lazy val daja = (project in file("."))
  .settings(
    name := "Daja",
    libraryDependencies ++= dajaDeps,

    Antlr4 / antlr4Version     := "4.13.2",
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
