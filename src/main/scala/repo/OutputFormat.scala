package mrico.repo

import org.sonatype.nexus.index.ArtifactInfo

trait OutputFormat {
  def format(info: ArtifactInfo): String
}

object SbtOutputFormat extends OutputFormat {

  def format(info: ArtifactInfo): String =
    List(info.groupId, info.artifactId, info.version).map("\"" + _ + "\"").mkString(" % ")
}

