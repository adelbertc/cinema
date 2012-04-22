package cinema.graph.mutable {
  import scala.collection.mutable
  import scala.io.Source
  import scala.util.Random
  import java.io.FileWriter
  import akka.jsr166y.ThreadLocalRandom
  
  object RandomWalkGraph {
    val RWLOOP = 2000
  }
  
  class RandomWalkGraph(parallel: Boolean = false) extends UndirectedGraph {
    /****************************
            CONSTRUCTORS
    ****************************/
    def this(filename: String, parallel: Boolean = false) = {
      this(parallel)
      for (line <- Source.fromFile(filename).getLines) {
        val linesplit = line.split(' ')
        addEdge(linesplit(0).toInt, linesplit(1).toInt)
      }
    }
  
    /****************************
             ACCESSORS
    ****************************/
    import RandomWalkGraph.RWLOOP
  
    def randomNeighbor(u: Int): Int = 
      if (parallel)
        adjList(u)(ThreadLocalRandom.current.nextInt(adjList(u).length))
      else
        adjList(u)(Random.nextInt(adjList(u).length))
  
    def getRandomVertices(k: Int): Vector[Int] = {
      val ret = new mutable.HashSet[Int]
      val vertexSet = getVertices
      val order = numberOfVertices
      while (ret.size != order && ret.size != k)
        ret += vertexSet(Random.nextInt(order))
      Vector() ++ ret
    }
  }
}

package cinema.graph.immutable {
  import scala.collection.mutable
  import scala.io.Source
  import scala.util.Random
  import java.io.FileWriter
  import akka.jsr166y.ThreadLocalRandom

  object RandomWalkGraph {
    val RWLOOP = 2000
  }

  class RandomWalkGraph(filename: String, parallel: Boolean = false) extends UndirectedGraph(filename) {
  
    /****************************
             ACCESSORS
    ****************************/
    import RandomWalkGraph.RWLOOP

    def randomNeighbor(u: Int): Int = 
      if (parallel)
        adjList(u)(ThreadLocalRandom.current.nextInt(adjList(u).length))
      else
        adjList(u)(Random.nextInt(adjList(u).length))

    def getRandomVertices(k: Int): Vector[Int] = {
      val ret = new mutable.HashSet[Int]
      val vertexSet = getVertices
      val order = numberOfVertices
      while (ret.size != order && ret.size != k)
        ret += vertexSet(Random.nextInt(order))
      Vector() ++ ret
    }
  }
}