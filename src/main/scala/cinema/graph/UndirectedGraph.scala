package cinema.graph.mutable {
  import scala.collection.mutable
  import scala.io.Source
  import scala.util.Random
  import akka.jsr166y.ThreadLocalRandom
  import cinema.graph.Graph
  
  class UndirectedGraph(parallel: Boolean = true) extends Graph {
    /****************************
            CONSTRUCTORS
    ****************************/
    protected val adjList = new mutable.HashMap[Int, mutable.ArrayBuffer[Int]]
  
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
    def addVertex(u: Int): Unit = {
      if (!(adjList contains u))
        adjList += (u -> new mutable.ArrayBuffer[Int])
    }
  
    def addEdge(u: Int, v: Int): Unit =  {
      if (u != v && !hasEdge(u, v)) {
        if (adjList contains u) adjList(u) += v
        else adjList += (u -> mutable.ArrayBuffer(v))
        if (adjList contains v) adjList(v) += u
        else adjList += (v -> mutable.ArrayBuffer(u))
      }
    }
  
    /****************************
              ACCESSORS
    ****************************/
    def numberOfVertices: Int = adjList size
  
    def hasVertex(u: Int): Boolean = adjList contains u
  
    def getVertices: Vector[Int] = Vector() ++ adjList.keysIterator
  
    def numberOfEdges: Int = adjList.foldLeft(0)(_ + _._2.size) / 2
  
    def hasEdge(u: Int, v: Int): Boolean = (adjList contains u) && (adjList(u) contains v)
  
    def getEdges: Vector[Tuple2[Int, Int]] = {
      val ret = new mutable.HashSet[Tuple2[Int, Int]]
      adjList.foreach(t =>
        t._2.foreach(v =>
          if (t._1 <= v) ret += (t._1 -> v)
          else ret += (v -> t._1)
        )
      ) 
      Vector() ++ ret
    }
  
    def degree(u: Int): Int = adjList(u) size
    
    def outDegree(u: Int): Int = degree(u)
    
    def inDegree(u: Int): Int = degree(u)
  
    def neighbors(u: Int): Vector[Int] = Vector() ++ adjList(u)
  
    def averageDegree(): Int = adjList.foldLeft(0)(_ + _._2.size) / numberOfVertices
    
    def slice(k: Int): Vector[Vector[Int]] = {
      val vertices = getVertices
      val slices = new mutable.ListBuffer[Vector[Int]]
      val stepSize = vertices.length / k
      var currentStep = 0
      var looper = 0
      while (looper != k) {
        if (looper != k - 1) {
          slices += vertices.slice(currentStep, stepSize)
          currentStep += stepSize
        } else {
          slices += vertices.slice(currentStep, vertices.length)
        }
        looper += 1
      }
      Vector() ++ slices
    }
    
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
  import akka.jsr166y.ThreadLocalRandom
  import cinema.graph.Graph

  class UndirectedGraph(filename: String, parallel: Boolean = true) extends Graph {
    /****************************
            CONSTRUCTORS
    ****************************/
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
    protected val adjList = tempAdjList.mapValues(v => Vector() ++ v).toMap
    
    /****************************
              ACCESSORS
    ****************************/
    def numberOfVertices: Int = adjList size

    def hasVertex(u: Int): Boolean = adjList contains u

    def getVertices: Vector[Int] = Vector() ++ adjList.keysIterator

    def numberOfEdges: Int = adjList.foldLeft(0)(_ + _._2.size) / 2

    def hasEdge(u: Int, v: Int): Boolean = (adjList contains u) && (adjList(u) contains v)

    def getEdges: Vector[Tuple2[Int, Int]] = {
      val ret = new mutable.HashSet[Tuple2[Int, Int]]
      adjList.foreach(t =>
        t._2.foreach(v =>
          if (t._1 <= v) ret += (t._1 -> v)
          else ret += (v -> t._1)
        )
      ) 
      Vector() ++ ret
    }

    def degree(u: Int): Int = adjList(u) size
    
    def outDegree(u: Int): Int = degree(u)
    
    def inDegree(u: Int): Int = degree(u)

    def neighbors(u: Int): Vector[Int] = Vector() ++ adjList(u)

    def averageDegree(): Int = adjList.foldLeft(0)(_ + _._2.size) / numberOfVertices
    
    def slice(k: Int): Vector[Vector[Int]] = {
      val vertices = getVertices
      val slices = new mutable.ListBuffer[Vector[Int]]
      val stepSize = vertices.length / k
      var currentStep = 0
      var looper = 0
      while (looper != k) {
        if (looper != k - 1) {
          slices += vertices.slice(currentStep, stepSize)
          currentStep += stepSize
        } else {
          slices += vertices.slice(currentStep, vertices.length)
        }
        looper += 1
      }
      Vector() ++ slices
    }
    
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

