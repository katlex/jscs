name := "Closure Compiler Server"

organization := "com.katlex"

moduleName := "ccs"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-filter" % "0.6.4",
  "net.databinder" %% "unfiltered-jetty" % "0.6.4",
  "org.clapper" %% "avsl" % "0.4",
  "org.clapper" % "grizzled-slf4j_2.10" % "1.0.1",
  "com.google.javascript" % "closure-compiler" % "r2180",
  "net.liftweb" %% "lift-util" % "2.5-M2",
  "net.databinder" %% "unfiltered-spec" % "0.6.4" % "test"
)

resolvers ++= Seq(
  "java m2" at "http://download.java.net/maven/2"
)
