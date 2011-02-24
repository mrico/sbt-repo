package mrico.repo

import org.sonatype.nexus.index._
import org.sonatype.nexus.index.updater._
import org.sonatype.nexus.index.creator._
import org.sonatype.nexus.index.context.{IndexCreator, IndexingContext}
import org.apache.maven.wagon.proxy.ProxyInfo
import org.apache.lucene.search._
import org.apache.lucene.search.BooleanClause.Occur
import org.apache.lucene.store.FSDirectory
import scala.collection.immutable.TreeSet
import scala.collection.jcl.Conversions._

import java.io.File

class RepositoryIndex(logger: sbt.Logger) extends PlexusContainerComponent with OrderedArtifactInfo {

  import RepositoryIndex._

  private val indexer = container.lookup(classOf[NexusIndexer])
  private val updater = container.lookup(classOf[IndexUpdater])

  private val FullIndex =
    new java.util.ArrayList[IndexCreator] {
      add(new MinimalArtifactInfoIndexCreator)
    }

  private def createResourceFetcher =
    new DatabinderHttpFetcher(logger)

  def addRepository(name: String, url: String) = {
    indexer.addIndexingContext(
      name,
      name,
      null: File,
      FSDirectory.getDirectory(indexBase + "/" + name),
      url,
      null: String,
      FullIndex)
  }

  def fetchAndUpdateIndices: Seq[Option[IndexUpdateResult]] =
     indexer.getIndexingContexts.map(e => fetchAndUpdateIndex(e._2)).toSeq

  def fetchAndUpdateIndex(ctx: IndexingContext): Option[IndexUpdateResult] = {
    val request = new IndexUpdateRequest(ctx)

    request.setResourceFetcher(createResourceFetcher)
    request.setProxyInfo(proxyInfo getOrElse null )

    try {
      Some(updater.fetchAndUpdateIndex(request))
    } catch {
      case ex:java.io.IOException =>
        logger.warn(ex.getMessage)
        None
    }
  }

  def query(s: String): Set[ArtifactInfo] = {
    val q = new BooleanQuery
    q.add(indexer.constructQuery(ArtifactInfo.GROUP_ID, s), Occur.SHOULD)
    q.add(indexer.constructQuery(ArtifactInfo.ARTIFACT_ID, s), Occur.SHOULD)

    val request = new FlatSearchRequest(q)

    try {
      val result = TreeSet(indexer.searchFlat(request).getResults.toArray(Array[ArtifactInfo]()): _*)
      result filter (_.classifier == null)
    } catch {
      case ex:BooleanQuery.TooManyClauses =>
        logger.error("Too many hits.")
        Set.empty
    }
  }
}

object RepositoryIndex {

  lazy val indexBase = System.getProperty("user.home", ".") + "/.sbt-dep/index"

  lazy val proxyInfo = {

    def getSystemProperty(k: String) = System.getProperty(k) match {
      case null => None
      case v => Some(v)
    }

    val hostOption = getSystemProperty("http.proxyHost")
    val port = getSystemProperty("http.proxyPort") getOrElse "80"

    val proxyInfo = hostOption.map { host =>
      val p = new ProxyInfo
      p.setType(ProxyInfo.PROXY_HTTP)
      p.setHost(host)
      p.setPort(port.toInt)
      p
    }

    proxyInfo
  }
}

