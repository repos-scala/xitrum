organization := "my.organization"

name         := "my_project"

version      := "1.0-SNAPSHOT"

scalaVersion := "2.9.0-1"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked"
)

// For Xitrum
resolvers += "Sonatype Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots"

// For Netty 4, remove this when Netty 4 is released
resolvers += "Local Maven Repository"       at "file://" + Path.userHome.absolutePath + "/.m2/repository"

libraryDependencies += "tv.cntt"        %% "xitrum"          % "1.1-SNAPSHOT"

libraryDependencies += "ch.qos.logback" %  "logback-classic" % "0.9.29"

// For "sbt console"
unmanagedClasspath in Compile <+= (baseDirectory) map { bd => Attributed.blank(bd / "config") }

// For "sbt run"
unmanagedClasspath in Runtime <+= (baseDirectory) map { bd => Attributed.blank(bd / "config") }
