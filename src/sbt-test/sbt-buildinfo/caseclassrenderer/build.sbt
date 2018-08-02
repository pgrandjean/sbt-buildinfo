import sbtbuildinfo.ScalaCaseClassRenderer

lazy val check = taskKey[Unit]("checks this plugin")

lazy val root = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    name := "helloworld",
    version := "0.1",
    scalaVersion := "2.11.8",
    buildInfoKeys := Seq(
      name,
      BuildInfoKey.map(version) { case (n, v) => "projectVersion" -> v.toDouble },
      scalaVersion,
      ivyXML,
      homepage,
      licenses,
      apiMappings,
      isSnapshot,
      "year" -> 2012,
      "sym" -> 'Foo,
      BuildInfoKey.action("buildTime") { 1234L },
      target),
    buildInfoOptions += BuildInfoOption.Traits("traits.MyCustomTrait"),
    buildInfoRenderFactory := ScalaCaseClassRenderer.apply,
    buildInfoPackage := "hello",
    homepage := Some(url("http://example.com")),
    licenses := Seq("MIT License" -> url("https://github.com/sbt/sbt-buildinfo/blob/master/LICENSE")),
    scalacOptions ++= Seq("-Ywarn-unused-import", "-Xfatal-warnings", "-Yno-imports"),
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
    check := {
      val f = (sourceManaged in Compile).value / "sbt-buildinfo" / ("%s.scala" format "BuildInfo")
      val lines = scala.io.Source.fromFile(f).getLines.toList
      lines match {
        case """package hello""" ::
          """""" ::
          """import scala.Predef._""" ::
          """import scala.Any""" ::
          """""" ::
          """/** This file was generated by sbt-buildinfo. */""" ::
          """case class BuildInfo(""" ::
          """  name: String,""" ::
          """  projectVersion: Any,""" ::
          """  scalaVersion: String,""" ::
          """  ivyXml: scala.xml.NodeSeq,""" ::
          """  homepage: scala.Option[java.net.URL],""" ::
          """  licenses: scala.collection.Seq[(String, java.net.URL)],""" ::
          """  apiMappings: Map[java.io.File, java.net.URL],""" ::
          """  isSnapshot: scala.Boolean,""" ::
          """  year: scala.Int,""" ::
          """  sym: scala.Symbol,""" ::
          """  buildTime: scala.Long,""" ::
          """  target: java.io.File""" ::
          """) extends traits.MyCustomTrait {""" ::
          """""" ::
          """}""" ::
          """""" ::
          """case object BuildInfo {""" ::
          """  def apply(): BuildInfo = new BuildInfo(""" ::
          """    name = "helloworld",""" ::
          """    projectVersion = 0.1,""" ::
          """    scalaVersion = "2.11.8",""" ::
          """    ivyXml = scala.collection.Seq(),""" ::
          """    homepage = scala.Some(new java.net.URL("http://example.com")),""" ::
          """    licenses = scala.collection.Seq(("MIT License" -> new java.net.URL("https://github.com/sbt/sbt-buildinfo/blob/master/LICENSE"))),""" ::
          """    apiMappings = Map(),""" ::
          """    isSnapshot = false,""" ::
          """    year = 2012,""" ::
          """    sym = 'Foo,""" ::
          """    buildTime = 1234L,""" ::
          targetInfo ::
          """  val get = apply()""" ::
          """  val value = apply()""" ::
          """}""" :: Nil if (targetInfo contains "target = new java.io.File(") =>
        case _ => sys.error("unexpected output: \n" + lines.mkString("\n"))
      }
      ()
    }
  )
