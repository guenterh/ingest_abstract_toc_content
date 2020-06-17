/*
 * Extracts media files from Fedora repository
 * Copyright (C) 2020  Memoriav
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import sbt._

object Dependencies {
  /*
  lazy val kafkaV = "2.3.1"
  lazy val log4jV = "2.11.2"
  lazy val scalatestV = "3.1.2"


   */
  lazy val flinkVersion = "1.10.1"

  /*
  lazy val kafkaClients = "org.apache.kafka" % "kafka-clients" % kafkaV
  lazy val log4jApi = "org.apache.logging.log4j" % "log4j-api" % log4jV
  lazy val log4jCore = "org.apache.logging.log4j" % "log4j-core" % log4jV
  lazy val log4jScala = "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0"
  lazy val log4jSlf4j = "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jV
  lazy val mariadbJdbcDriver = "org.mariadb.jdbc" % "mariadb-java-client" % "2.6.0"
  lazy val memobaseServiceUtils = "org.memobase" % "memobase-service-utilities" % "1.2.1"
  lazy val scalatic = "org.scalactic" %% "scalactic" % scalatestV
  lazy val scalaTest = "org.scalatest" %% "scalatest" % scalatestV
  lazy val sprayJson = "io.spray" %%  "spray-json" % "1.3.5"



   */
  lazy val iomonixtest = "io.monix" %% "minitest" % "2.8.2"

  lazy val mysql = "mysql" % "mysql-connector-java" % "5.1.38"
  lazy val quill_jdbc = "io.getquill" %% "quill-jdbc" % "3.5.1"
  lazy val quill_core = "io.getquill" %% "quill-core" % "3.5.1"

  /*
  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-table-planner" % flinkVersion,
  "org.apache.flink" %% "flink-table-api-scala-bridge" % flinkVersion,

   */
  lazy val scala_xml = "org.scala-lang.modules" %% "scala-xml" % "1.3.0"
  //"org.apache.flink" %% "flink-connector-kafka" % "1.8.0",
  lazy val marc_xml_fields = "org.swissbib.slsp" % "marcxml-fields" % "0.5.2"
  lazy val scala_test = "org.scalatest" %% "scalatest" % "3.0.8" % Test
  lazy val scala_mock = "org.scalamock" %% "scalamock" % "4.2.0" % Test



}
