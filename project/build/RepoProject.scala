import sbt._

class RepoProject(info: ProjectInfo) extends ProcessorProject(info) {

  val nexusIndexer = "org.sonatype.nexus" % "nexus-indexer" % "3.0.4"
  val dispatch = "net.databinder" %% "dispatch-http" % "0.7.8"
}

