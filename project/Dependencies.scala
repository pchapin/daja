import sbt._

// See: https://www.scala-sbt.org/1.x/docs/Organizing-Build.html
object Dependencies {

  // Versions
  lazy val scalaTestVersion = "3.2.17"
  lazy val scalaGraphCoreVersion = "1.13.6"  // Version 2.0.0 seems very different.

  // Libraries
  val scalactic = "org.scalactic" %% "scalactic" % scalaTestVersion
  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion
  val scalaGraphCore = "org.scala-graph" %% "graph-core" % scalaGraphCoreVersion

  // Projects

  // When there is a version of Graph-for-Scala explicitly labeled for Scala 3 in Maven, the
  // following line can be uncommented here and the unmanagedJar removed from build.sbt.
  //val dajaDeps = Seq(scalaGraphCore, scalactic, scalaTest % Test)
  
  val dajaDeps = Seq(scalactic, scalaTest % Test)
  val tigerDeps   = Seq(scalactic, scalaTest % Test)
  val dragonDeps  = Seq(scalactic, scalaTest % Test)

}
