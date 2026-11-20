import org.scalajs.linker.interface.ModuleInitializer

ThisBuild / scalaVersion := "3.9.0"

lazy val cardgame = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalacOptions ++= Seq("-deprecation", "-feature", "-explain", "-Werror"),
    libraryDependencies += "org.scalameta" %% "munit" % "1.3.6" % Test,
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    libraryDependencies += "org.scala-js" %% "scalajs-dom" % "2.8.1",
    Compile / run := {
      Def.sequential(
        Compile / compile,
        Compile / fastLinkJS
      ).value
      () // discard the result value explicitly
    }
  )
