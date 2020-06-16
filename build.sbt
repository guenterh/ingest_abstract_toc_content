scalaVersion := "2.12.10"

//scalaVersion := "2.13.2"

ThisBuild / resolvers ++= Seq(
  "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  "marcxml-fields" at "https://gitlab.com/api/v4/projects/12974592/packages/maven",
  Resolver.mavenLocal
)

// https://mvnrepository.com/artifact/io.getquill/quill-jdbc


val flinkVersion = "1.10.1"

//8.0.17
//5.1.38
libraryDependencies ++= Seq(
"io.monix" %% "minitest" % "2.8.2" % "test",

  "mysql" % "mysql-connector-java" % "5.1.38",
  "io.getquill" %% "quill-jdbc" % "3.5.1",
  "io.getquill" %% "quill-core" % "3.5.1",

  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-table-planner" % flinkVersion,
  "org.apache.flink" %% "flink-table-api-scala-bridge" % flinkVersion,
  "org.scala-lang.modules" %% "scala-xml" % "1.3.0",
  "org.apache.flink" %% "flink-connector-kafka" % "1.8.0",
  "org.swissbib.slsp" % "marcxml-fields" % "0.5.2",
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "org.scalamock" %% "scalamock" % "4.2.0" % Test
)






testFrameworks += new TestFramework("minitest.runner.Framework")
