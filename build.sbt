name := "testMongo2"
version := "0.1"
scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0",
  "org.slf4j" % "jul-to-slf4j" % "1.7.28",
  "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime,
  "org.json4s" %% "json4s-jackson" % "4.0.5",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Test,
  "org.apache.logging.log4j" % "log4j-core" % "2.11.1"

)
