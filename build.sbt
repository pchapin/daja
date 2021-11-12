import Dependencies._

enablePlugins(Antlr4Plugin)

ThisBuild / organization  := "org.pchapin"
ThisBuild / version       := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion  := "2.12.8"
ThisBuild / scalacOptions :=
  Seq("-encoding", "UTF-8",
      "-feature",
      "-deprecation",
      "-unchecked",
      "-Ywarn-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-infer-any",
      "-Ywarn-unused-import",
      "-Ywarn-value-discard")

Test / logBuffered := false

lazy val daja = (project in file("."))
  .settings(
    name := "Daja",
    libraryDependencies ++= dajaDeps,

    Antlr4 / antlr4Version     := "4.7.2",
    Antlr4 / antlr4PackageName := Some("org.pchapin.daja"),
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
    //excludeFilter in unmanagedSources := HiddenFileFilter || "*slem*"
  )

