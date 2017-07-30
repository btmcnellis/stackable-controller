val commonSettings = Seq(
  organization := "com.jaroop",
  version := "0.7.0-SNAPSHOT",
  scalaVersion := "2.12.3",
  crossScalaVersions := scalaVersion.value :: Nil,
  scalacOptions ++= Seq(
    "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
    "-encoding", "utf-8",                // Specify character encoding used by source files.
    "-explaintypes",                     // Explain type errors in more detail.
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
    "-Xlint:_,-unused",                  // Enable lint warnings except for unused imports, parameters, etc.
    "-Ywarn-dead-code",                  // Warn when dead code is identified.
    "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
    "-Ywarn-numeric-widen",              // Warn when numerics are widened.
    "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
  )
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
      "com.typesafe.play" %% "play" % play.core.PlayVersion.current % "provided",
      guice
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
    libraryDependencies ++= Seq(
      play.sbt.Play.autoImport.jdbc,
      play.sbt.Play.autoImport.specs2 % "test",
      "com.typesafe.play"  %% "play"                         % play.core.PlayVersion.current,
      "com.h2database"     %  "h2"                           % "1.4.195",
      "org.scalikejdbc"    %% "scalikejdbc"                  % "3.0.1",
      "org.scalikejdbc"    %% "scalikejdbc-config"           % "3.0.1",
      "org.scalikejdbc"    %% "scalikejdbc-play-initializer" % "2.6.0",
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
