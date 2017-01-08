lazy val root = (project in file("."))
  .settings(
    organization := "com.keithblaha",
    version := "0.1.0",
    scalaVersion := "2.12.1",

    mainClass in assembly := Some("com.keithblaha.Main"),

    scalaSource in Compile := baseDirectory.value / "src",
    resourceDirectory in Compile := baseDirectory.value / "resources",
    scalaSource in Test := baseDirectory.value / "test" / "src",
    resourceDirectory in Test := baseDirectory.value / "test" / "resources",

    libraryDependencies ++= Seq(
      "flat" %% "flat" % "0.4.0"
    ),

    fork in Test := true,
    javaOptions in Test +="-Dlogger.resource=logback-test.xml"
  )

