package mrico.repo

import org.sonatype.nexus.index._

private[repo] trait OrderedArtifactInfo {

  implicit def orderdArtifactInfo(info: ArtifactInfo) = new Ordered[ArtifactInfo] {

      def compare(that: ArtifactInfo) =
        compare(info.groupId, that.groupId,
        compare(info.artifactId, that.artifactId,
        compare(info.version, that.version,
        compare(info.classifier, that.classifier, 0))))

      private def compare(a: String, b: String, f: => Int): Int =
        nullSafe(a).compareTo(nullSafe(b)) match {
          case 0 => f
          case x => x
        }

      private def nullSafe(s: String) =
        if(s == null) ""
        else s
  }
}

