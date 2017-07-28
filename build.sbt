val commonSettings = Seq(
  organization := "jp.t2v",
  version := "0.7.0-SNAPSHOT",
  scalaVersion := "2.11.11",
  crossScalaVersions := scalaVersion.value :: Nil,
  scalacOptions ++= Seq("-unchecked")
)

resolvers ++= Seq(
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases"
)

lazy val core = (project in file("core"))
  .settings(
    commonSettings,
    name := "stackable-controller",
    publishTo := publishDestination(version.value),
    publishMavenStyle := true,
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % play.core.PlayVersion.current % "provided"
    ),
    sbtPlugin := false,
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { x => false },
    pomExtra := pom
  )

lazy val sample = (project in file("sample"))
  .enablePlugins(play.sbt.PlayScala)
  .settings(
    commonSettings,
    routesGenerator := StaticRoutesGenerator,
    libraryDependencies ++= Seq(
      play.sbt.Play.autoImport.jdbc,
      play.sbt.Play.autoImport.specs2 % "test",
      "com.typesafe.play"  %% "play"                         % play.core.PlayVersion.current,
      "org.scalikejdbc"    %% "scalikejdbc"                  % "2.5.2",
      "org.scalikejdbc"    %% "scalikejdbc-config"           % "2.5.2",
      "org.scalikejdbc"    %% "scalikejdbc-play-initializer" % "2.5.3",
      "org.slf4j"          %  "slf4j-simple"                 % "[1.7,)"
    )
  )
  .dependsOn(core)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .aggregate(core, sample)

// TODO: Replace this with the correct publish information
def publishDestination(v: String) = {
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

lazy val pom = {
  <url>https://github.com/t2v/stackable-controller</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:t2v/stackable-controller</url>
    <connection>scm:git:git@github.com:t2v/stackable-controller</connection>
  </scm>
  <developers>
    <developer>
      <id>gakuzzzz</id>
      <name>gakuzzzz</name>
      <url>https://github.com/gakuzzzz</url>
    </developer>
  </developers>
}
