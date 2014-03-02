name := "tweet-poetry"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.twitter4j"     % "twitter4j-stream" % "3.0.3",
  "org.reactivemongo" %% "reactivemongo" % "0.10.0",
  "org.mockito"       %   "mockito-all"   % "1.9.5",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2"
)

play.Project.playScalaSettings
