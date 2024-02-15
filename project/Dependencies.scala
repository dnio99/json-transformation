import sbt.*

object Dependencies {

  object Versions {
    val zio = "2.0.15"
    val zioConfig = "3.0.2"
    val zioLogging = "2.1.2"
    val circe = "0.14.3"
    val scalaTest = "3.2.16"
    val tapir = "1.1.3"
    val http4s = "0.23.23"
    val polyglot = "23.1.2"
    val fs2Data = "1.10.0"
  }

  val zioLogging = Seq(
    "dev.zio" %% "zio-logging" % Versions.zioLogging,
    "dev.zio" %% "zio-logging-slf4j" % Versions.zioLogging
  )

  val logging = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.11"
  ) ++ zioLogging

  val scalaTest = Seq(
    "org.scalatest" %% "scalatest-funsuite" % Versions.scalaTest % "test"
  )
  val zioTest = Seq(
    "dev.zio" %% "zio-test" % Versions.zio % "test",
    "dev.zio" %% "zio-test-magnolia" % Versions.zio % "test", // optional
    "dev.zio" %% "zio-test-sbt" % Versions.zio % "test"
  )

  val zioConfig = Seq(
    "dev.zio" %% "zio-config" % Versions.zioConfig,
    "dev.zio" %% "zio-config-magnolia" % Versions.zioConfig,
    "dev.zio" %% "zio-config-typesafe" % Versions.zioConfig
  )

  val zioCore = Seq(
    "dev.zio" %% "zio" % Versions.zio,
    "dev.zio" %% "zio-interop-cats" % "3.3.0"
  )

  val zioStream = Seq(
    "dev.zio" %% "zio-streams" % Versions.zio
  )

  val zioNio = Seq(
    "dev.zio" %% "zio-nio" % "2.0.1"
  )

  val http4sServer = Seq(
    "org.http4s" %% "http4s-ember-server"
  ).map(_ % Versions.http4s)

  val circeCore = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % Versions.circe)

  val polyglot = Seq(
    "org.graalvm.polyglot" % "polyglot",
    "org.graalvm.polyglot" % "js"
  ).map(_ % Versions.polyglot)

  val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-zio",
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server-zio",
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe",
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle"
  ).map(_ % Versions.tapir)

  val fs2Data = Seq(
    "org.gnieh" %% "fs2-data-csv",
    "org.gnieh" %% "fs2-data-csv-generic",
    "org.gnieh" %% "fs2-data-text"
  ) map (_ % Versions.fs2Data)

  val dependencies =
    logging ++ scalaTest ++ zioTest ++ zioConfig ++ zioCore ++ zioCore ++ zioNio ++ circeCore ++ circeCore ++ polyglot ++ tapir ++ http4sServer ++ fs2Data
}
