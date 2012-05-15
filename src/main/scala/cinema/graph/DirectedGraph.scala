package cinema.graph.mutable {
  import scala.collection.mutable
  import scala.io.Source
  import scala.util.Random
  import akka.jsr166y.ThreadLocalRandom
  import cinema.graph.Graph

  class DirectedGraph(parallel: Boolean = true) extends UndirectedGraph(parallel) {
    /****************************
            CONSTRUCTORS
    ****************************/
    def this(filename: String, parallel: Boolean = true) = {
      this(parallel)
      for (line <- Source.fromFile(filename).getLines) {
        val linesplit = line.split(' ')
        addEdge(linesplit(0).toInt, linesplit(1).toInt) 
      }
    }

    /****************************
              MODIFIERS
    ****************************/
    override def addEdge(u: Int, v: Int): Unit =  {
      if (u != v && !hasEdge(u, v)) {
        if (adjList contains u) adjList(u) += v
        else adjList += (u -> mutable.ArrayBuffer(v))
      }
    }

    /****************************
              ACCESSORS
    ****************************/
    override def numberOfEdges: Int = adjList.foldLeft(0)(_ + _._2.size)

    override def getEdges: Vector[Tuple2[Int, Int]] = {
      val ret = new mutable.HashSet[Tuple2[Int, Int]]
      adjList.foreach(t =>
        t._2.foreach(v =>
          ret += (t._1 -> v)
        )
      ) 
      Vector() ++ ret
    }

    override def inDegree(u: Int): Int = {
      var ret = 0
      adjList.foreach(t => if (hasEdge(t._1, u)) ret += 1)
      ret
    }
  }
}

package cinema.graph.immutable {
  import scala.collection.mutable
  import scala.io.Source
  import scala.util.Random
  import akka.jsr166y.ThreadLocalRandom
  import cinema.graph.Graph

  class DirectedGraph(filename: String, parallel: Boolean = true) extends UndirectedGraph(filename, parallel) {
    /*
    val tempAdjList = new mutable.HashMap[Int, mutable.ArrayBuffer[Int]]
    for (line <- Source.fromFile(filename).getLines) {
      val linesplit = line.split(' ')
      val u = linesplit(0).toInt
      val v = linesplit(1).toInt
      if (u != v && !((tempAdjList contains u) && (tempAdjList(u) contains v))) {
        if (tempAdjList contains u) tempAdjList(u) += v
        else tempAdjList += (u -> mutable.ArrayBuffer(v))
        if (tempAdjList contains v) tempAdjList(v) += u
        else tempAdjList += (v -> mutable.ArrayBuffer(u))
      }
    }
    protected val adjList = tempAdjList.mapValues(v => Vector() ++ v).toMap*/

    override def numberOfEdges: Int = adjList.foldLeft(0)(_ + _._2.size) / 2

    override def getEdges: Vector[Tuple2[Int, Int]] = {
      val ret = new mutable.HashSet[Tuple2[Int, Int]]
      adjList.foreach(t =>
        t._2.foreach(v =>
          if (t._1 <= v) ret += (t._1 -> v)
          else ret += (v -> t._1)
        )
      ) 
      Vector() ++ ret
    }

    override def inDegree(u: Int): Int = {
      var ret = 0
      adjList.foreach(t => if (hasEdge(t._1, u)) ret += 1)
      ret
    }
  }
}