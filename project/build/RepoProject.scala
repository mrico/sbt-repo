import sbt._

class RepoProject(info: ProjectInfo) extends ProcessorProject(info) {  
  override def managedStyle = ManagedStyle.Maven  
  lazy val publishTo = Resolver.file("GitHub Pages", new java.io.File("../mrico.github.com/maven/"))
  
  val nexusIndexer = "org.sonatype.nexus" % "nexus-indexer" % "3.0.4"
  val dispatch = "net.databinder" %% "dispatch-http" % "0.7.8"
}

