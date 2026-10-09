import sbt.IO
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.*


lazy val runMVisu = taskKey[Unit]("runMVisu")

val ScalaVersion = "3.7.3"
lazy val esbuildMain = s"scala-${ScalaVersion}/esbuild/main"

lazy val `method-visu` = project.in(file("."))
  .enablePlugins(ScalaJSEsbuildPlugin)
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
    scalaVersion := ScalaVersion,
    libraryDependencies += "com.raquo" %%% "laminar" % "17.2.1",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "4.1.0",
    scalacOptions ++= Seq("-encoding", "utf-8", "-deprecation", "-feature"),
    scalaJSUseMainModuleInitializer := true,
    externalNpm := target.value / esbuildMain,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
    },
    runMVisu := {
      val smResource = (Compile / resourceDirectory).value
      val jsBuild = (Compile / fullOptJS / esbuildBundle).value
      IO.copyFile(target.value / s"${esbuildMain}/out/main.js", target.value / "esbuild/js/methodVisu.js")
      IO.copyDirectory(smResource, target.value)
    }
  )
