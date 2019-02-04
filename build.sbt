name := "QuadTree"

version := "0.1"

scalaVersion := "2.12.8"

val copyFastOpt = taskKey[File]("Return fastOpt file.")
lazy val fastOptCompileCopy = taskKey[Unit]("Compile and generate html file, with fastOpt.")

val copyFullOpt = taskKey[File]("Return fullOpt file.")
lazy val fullOptCompileCopy = taskKey[Unit]("Compile and generate html file, with fullOpt.")


libraryDependencies ++= Seq(
  "org.scalatest" %%% "scalatest" % "3.0.5" % "test",
  "org.scala-js" %%% "scalajs-dom" % "0.9.6",
  "com.lihaoyi" %%% "scalatags" % "0.6.7"
)

enablePlugins(ScalaJSPlugin)

scalaJSUseMainModuleInitializer := true

copyFastOpt := {
  (fastOptJS in Compile).value.data
}
copyFullOpt := {
  (fullOptJS in Compile).value.data
}


fullOptCompileCopy := {

  val jsDirectory = copyFullOpt.value

  val sourceHtmlLines = IO.readLines(baseDirectory.value / "sourcehtml/index.html")
  val sourceJSLines = IO.readLines(jsDirectory)

  val newLines = sourceHtmlLines.flatMap(line => {
    if (line == "<!-- fill here -->")
      "<script type=\"text/javascript\">" +: sourceJSLines :+ "</script>"
    else List(line)
  })

  IO.writeLines(
    baseDirectory.value / "html/index.html",
    newLines
  )


}

fastOptCompileCopy := {

  /**
    * Compiles the mainProcess project, and copy paste it in the electron/mainprocess directory.
    */
  val jsDirectory = copyFastOpt.value

  val sourceHtmlLines = IO.readLines(baseDirectory.value / "sourcehtml/index.html")
  val sourceJSLines = IO.readLines(jsDirectory)

  val newLines = sourceHtmlLines.flatMap(line => {
    if (line == "<!-- fill here -->")
      "<script type=\"text/javascript\">" +: sourceJSLines :+ "</script>"
    else List(line)
  })

  IO.writeLines(
    baseDirectory.value / "html/index.html",
    newLines
  )


}
