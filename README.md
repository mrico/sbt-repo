sbt-repo is a [Simple Build Tool](http://code.google.com/p/simple-build-tool/) processor to query public maven repositories. 

# Usage

1. Launch sbt console

2. Enter the following commands
<pre>
> *sbtRepo at http://mrico.github.com/maven/
...
> *repo is eu.mrico sbt-repo 0.1.0
...
</pre>

3. Indexing maven repositories
<pre>
> repo update
...
</pre>

4. Search something
<pre>
> repo search scalatest
...
</pre>
