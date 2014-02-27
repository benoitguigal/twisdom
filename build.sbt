name := "tweet-poetry"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.twitter4j" % "twitter4j-stream" % "3.0.3",
  "org.reactivemongo" %% "reactivemongo" % "0.10.0"
)

play.Project.playScalaSettings
