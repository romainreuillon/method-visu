import org.scalajs.linker.interface.ModuleSplitStyle
import sbt.librarymanagement

lazy val `method-visu` = project.in(file("."))
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .enablePlugins(ScalaJSPlugin) // Enable the Scala.js plugin in this project
  .settings(
    scalaVersion := "3.7.3",
    scalacOptions ++= Seq("-encoding", "utf-8", "-deprecation", "-feature"),

    // Tell Scala.js that this is an application with a main method
    scalaJSUseMainModuleInitializer := true,

    /* Configure Scala.js to emit modules in the optimal way to
     * connect to Vite's incremental reload.
     * - emit ECMAScript modules
     * - emit as many small modules as possible for classes in the "testvite" package
     * - emit as few (large) modules as possible for all other classes
     *   (in particular, for the standard library)
     */
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("draftvisu")))
    },


    // Depend on Laminar
    libraryDependencies += "com.raquo" %%% "laminar" % "17.2.1",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "4.1.0", // SBT
    libraryDependencies += "org.openmole" %%% "scala-js-plotlyjs" % "1.8.1",

    //stEnableLongApplyMethod := true,

    //stIgnore += "plotly.js-dist-min",
    // Tell ScalablyTyped that we manage `npm install` ourselves
    externalNpm := baseDirectory.value
  )
