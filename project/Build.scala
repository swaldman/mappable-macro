import sbt._

object MchangeCommonsScalaMacroBuild extends Build {

  val nexus = "https://oss.sonatype.org/"
  val nexusSnapshots = nexus + "content/repositories/snapshots";
  val nexusReleases = nexus + "service/local/staging/deploy/maven2";

  val projName = "casemap";

  val mySettings = Seq( 
    Keys.name := projName, 
    Keys.version := "0.0.1", 
    Keys.scalaVersion := "2.10.1",
    Keys.scalacOptions ++= Seq("-deprecation", "-Xlog-free-terms" /*, "-Ymacro-debug-lite" */),
    Keys.resolvers += ("snapshots" at nexusSnapshots )
  );

  val dependencies = Seq(
    "org.scala-lang" % "scala-reflect" % "2.10.1",
    "org.scala-lang" % "scala-compiler" % "2.10.1",
//    "com.typesafe.akka" %% "akka-actor" % "2.1+",
    "org.specs2" %% "specs2" % "1.14+" % "test"
//    "com.mchange" %% "mchange-commons-scala" % "0.4.0-SNAPSHOT" changing()
  );


  override lazy val settings = super.settings ++ mySettings;

  lazy val mainProject = Project(
    id = projName,
    base = file("."),
    settings = Project.defaultSettings ++ (Keys.libraryDependencies ++= dependencies)
  );

}
