
ThisBuild / scalaVersion     := "2.13.8"

val chiselVersion = "3.5.4"

lazy val root = (project in file("."))
  .settings(
    name := "MyTest",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % chiselVersion,
      "com.typesafe.play" %% "play-json"         % "2.9.2",
      "org.reflections"   %  "reflections"       % "0.9.12",
      "edu.berkeley.cs" %% "chiseltest" % "0.5.1" % "test",
      "edu.berkeley.cs" %% "chisel-iotesters" % "2.5.4", // iotester seems much faster than chiseltest
      "org.typelevel" %% "cats-core" % "2.9.0",
      "org.typelevel" %% "cats-free" % "2.9.0",
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-P:chiselplugin:genBundleElements",
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % chiselVersion cross CrossVersion.full),
  )

