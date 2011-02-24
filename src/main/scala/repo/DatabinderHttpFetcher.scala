package mrico.repo

import java.io._
import org.sonatype.nexus.index.updater.{ResourceFetcher, AbstractResourceFetcher}
import dispatch._
import Http._

private [repo] class DatabinderHttpFetcher(sbtLogger: sbt.Logger) extends AbstractResourceFetcher with ResourceFetcher {

  private var reposOption: Option[(String,String)] = None

  def connect(id: String, url: String) {
    this.reposOption = Some((id, url))
  }

  def retrieve(name: String, targetFile: File) {
    val repos = reposOption.getOrElse(error("No repository defined. Call connect first!"))

    val http = new Http with SbtLogger {
      val logger = sbtLogger
    }

    try {
      http(repos._2 / name >>> new FileOutputStream(targetFile))
    } catch {
      case StatusCode(404, msg) =>
        throw new FileNotFoundException(msg)
    }
  }

  def disconnect() {}
}

trait SbtLogger extends Http {

  val logger: sbt.Logger

  override def make_logger = new Logger {
    def info(msg: String, items: Any*) {
      logger.info(msg.format(items: _*))
    }
  }
}

