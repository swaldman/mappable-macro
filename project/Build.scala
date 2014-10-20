import sbt._

object MappableMacroBuild extends Build {

  val nexus = "https://oss.sonatype.org/"
  val nexusSnapshots = nexus + "content/repositories/snapshots";
  val nexusReleases = nexus + "service/local/staging/deploy/maven2";

  val projName = "mappable-macro";

  val mySettings = Seq( 
    Keys.organization := "com.mchange",
    Keys.name := projName, 
    Keys.version := "0.0.1-SNAPSHOT", 
    Keys.scalaVersion := "2.10.1",
    Keys.publishTo <<= Keys.version { 
      (v: String) => {
	if (v.trim.endsWith("SNAPSHOT"))
	  Some("snapshots" at nexusSnapshots )
	else
	  Some("releases"  at nexusReleases )
      }
    },
    Keys.scalacOptions ++= Seq("-deprecation", "-Xlog-free-terms" /*, "-Ymacro-debug-lite" */),
    Keys.resolvers += ("snapshots" at nexusSnapshots ),
    Keys.pomExtra := pomExtraXml
  );

  val dependencies = Seq(
    "org.scala-lang" % "scala-reflect" % "2.10.1",
    "org.scala-lang" % "scala-compiler" % "2.10.1",
    "org.specs2" %% "specs2" % "1.14+" % "test"
  );


  override lazy val settings = super.settings ++ mySettings;

  lazy val mainProject = Project(
    id = projName,
    base = file("."),
    settings = Project.defaultSettings ++ (Keys.libraryDependencies ++= dependencies)
  );

  val pomExtraXml = (
      <url>https://github.com/swaldman/mappable-macro</url>
      <licenses>
        <license>
          <name>GNU Lesser General Public License, Version 2.1</name>
          <url>http://www.gnu.org/licenses/lgpl-2.1.html</url> 
          <distribution>repo</distribution>
        </license>
        <license>
          <name>Eclipse Public License, Version 1.0</name>
          <url>http://www.eclipse.org/org/documents/epl-v10.html</url> 
          <distribution>repo</distribution>
        </license>
     </licenses>
     <scm>
       <url>git@github.com:swaldman/mappable-macro.git</url>
       <connection>scm:git:git@github.com:swaldman/mappable-macro.git</connection>
     </scm>
     <developers>
       <developer>
         <id>swaldman</id>
         <name>Steve Waldmam</name>
         <email>swaldman@mchange.com</email>
       </developer>
     </developers>
  );
}

