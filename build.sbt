val projectName = "json-transformation"

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin, DockerPlugin, JavaAppPackaging, JavaAgent)
  .settings(commonSettings(projectName) *)
  .settings(buildSettings(projectName))
  .settings(dockerSettings(Seq(6600)))
  .settings(
    libraryDependencies ++= Dependencies.dependencies,
    Compile / mainClass := Some("json.transformation.application.Loader")
  )

def commonSettings(module: String, languageVersion: String = "2.13.12") =
  Seq[Setting[_]](
    name := module,
    organization := "json.transformation",
    resolvers += "jitpack".at("https://jitpack.io"),
    // Disable doc generate
    Compile / doc / sources := Seq.empty,
    Compile / packageDoc / publishArtifact := false,
    versionScheme := Some("early-semver"),
    scalaVersion := languageVersion,
    scalafmtOnCompile := true,
    scalacOptions ++= Seq(
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-encoding",
      "utf-8", // Specify character encoding used by source files.
      "-explaintypes", // Explain type errors in more detail.
      "-Ymacro-annotations",
      "-Vimplicits",
      "-Vtype-diffs",
      "-Wunused"
    ),
    fork := true,
    run / connectInput := true,
    Global / concurrentRestrictions += Tags.limit(Tags.Test, 1),
    addCompilerPlugin(
      "org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full
    )
  )

def buildSettings(module: String) = Seq(
  buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
  buildInfoPackage := s"json.transformation",
  buildInfoOptions += BuildInfoOption.BuildTime
)


def dockerSettings(ports: Seq[Int]) = Seq(
  Docker / maintainer := "json transformation",
  packageName := s"${projectName}",
  dockerBaseImage := "ghcr.io/graalvm/jdk-community:21",
  dockerRepository := Some("cr.mesoor.com/production"),
  dockerUsername := None,
  dockerExposedPorts := ports
)