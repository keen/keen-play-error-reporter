name := "keen-error-reporter"

organization := "io.keen"

version := "1.0.2"

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.10.4", "2.11.6")

S3Resolver.defaults

s3region := com.amazonaws.services.s3.model.Region.US_West

publishMavenStyle := false

resolvers ++= Seq[Resolver](
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/",
  s3resolver.value("Keen Release S3 bucket", s3("keen-maven-release")).withIvyPatterns,
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
)

scalacOptions ++= Seq("-feature", "-Yrangepos")

libraryDependencies ++= Seq(
  "com.typesafe.play"       %% "play"                           % "2.3.8",
  "net.kencochrane.raven"   %  "raven"                          % "6.0.0",
  "org.specs2"              %% "specs2-core"                    % "3.4"     % "test"
)

publishTo := {
  val suffix = if (isSnapshot.value) "snapshot" else "release"
  Some(s3resolver.value("keen-maven-"+suffix, s3("keen-maven-"+suffix)) withIvyPatterns)
}
