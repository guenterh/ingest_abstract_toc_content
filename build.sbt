import Dependencies._
import sbt.Keys.testFrameworks

ThisBuild / scalaVersion := "2.12.11"
ThisBuild / organization := "org.swissbib"
ThisBuild / organizationName := "swissbib"
ThisBuild / git.gitTagToVersionNumber := { tag: String =>
  if (tag matches "[0-9]+\\..*") Some(tag)
  else None
}

lazy val root = (project in file("."))
  .enablePlugins(GitVersioning)
  .settings(
    name := "fetch-abstracts-tocs",
    assemblyJarName in assembly := "app.jar",
    test in assembly := {},
    mainClass in assembly := Some("Main"),
    resolvers ++= Seq(
      //"Memobase Utils" at "https://dl.bintray.com/jonas-waeber/memobase"
      "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
      "marcxml-fields" at "https://gitlab.com/api/v4/projects/12974592/packages/maven",
      Resolver.mavenLocal
    ),

    libraryDependencies ++= Seq(
      iomonixtest % Test,
      mysql,
      quill_core,
      quill_jdbc,
      scala_xml,
      marc_xml_fields,
      scala_test,
      scala_mock,
      minitest
      //argparser
      )
  )



