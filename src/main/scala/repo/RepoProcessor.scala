package mrico.repo

import sbt._
import processor.BasicProcessor

class RepoProcessor extends BasicProcessor {

  private val outputFormat = SbtOutputFormat

  def apply(project: Project, args: String) {
    implicit val currentProject = project

    if(args startsWith "search ")
      search(args.substring(7))
    else if(args == "update")
      update
    else
      project.log.error("unknown command %s".format(args))
  }

  def update(implicit project: Project) {
    project.log.info("updating repository index.")
    val index = setupRepositoryIndex
    index.fetchAndUpdateIndices
  }

  def search(arg: String)(implicit project: Project) {
    project.log.info("searching for '%s'".format(arg))

    val index = setupRepositoryIndex

    index.query(arg) match {
      case xs if xs.isEmpty => project.log.info("no match found.")
      case xs => xs.foreach(a => println(outputFormat.format(a)))
    }
  }

  def setupRepositoryIndex(implicit project:Project) = {
    val index = new RepositoryIndex(project.log)

    index.addRepository(DefaultMavenRepository.name, DefaultMavenRepository.root)
    index.addRepository(ScalaToolsReleases.name, ScalaToolsReleases.root)

    project match {
      case rp:ReflectiveRepositories =>
        for(r <- rp.repositories) {
          r match {
            case repo: MavenRepository =>
              if(isSupportedProtocol(repo.root)) {
                index.addRepository(repo.name, repo.root)
                project.log.info("repository \"%s\" at %s".format(repo.name, repo.root))
              } else {
                project.log.warn("repository \"%s\" at %s not added. Protocol not supported.".format(repo.name, repo.root))
              }
            case x =>
              project.log.warn("repository %s is not a maven repository".format(x.name))
          }
        }
      case _ =>
    }

    index
  }

  private def isSupportedProtocol(url: String) =
    url.toLowerCase matches "^http(s)?://.*$"

}

