package mrico.repo

import org.codehaus.plexus.DefaultPlexusContainer

trait PlexusContainerComponent {

   val container = withClassLoaderOf(classOf[DefaultPlexusContainer], new DefaultPlexusContainer)

  /**
   * Executes the function f within the ContextClassLoader of 'classOf'.
   * After execution the original ClassLoader will be restored.
   */
  private def withClassLoaderOf[T,B](classOf: Class[T], f: => B): B = {
    val thread = Thread.currentThread
    val old = thread.getContextClassLoader
    thread.setContextClassLoader(classOf.getClassLoader)
    val result = f
    thread.setContextClassLoader(old)
    result
  }
}

