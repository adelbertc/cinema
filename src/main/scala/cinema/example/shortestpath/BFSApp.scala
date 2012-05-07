package cinema.example.shortestpath

import scala.collection.immutable
import scala.collection.mutable
import cinema.crew._
import cinema.graph.Graph
import cinema.graph.immutable.UndirectedGraph

object BFSApp {
  def shortestPath(myGraph: Graph, u: Int): immutable.Map[Int, Int] = {
    val result = new mutable.HashMap[Int, Int]
    myGraph.getVertices.foreach(vertex => result(vertex) = -1)
    result(u) = 0
    
    val q = new mutable.Queue[Int]
    q enqueue u
    
    var currentRadius = 0
    while (q.size != 0) {
      currentRadius += 1
      val v = q.dequeue()
      for (nbor <- myGraph.neighbors(v) if result(nbor) == -1) {
        result(nbor) = currentRadius
        q enqueue nbor
      }
    }
    result.toMap
    //immutable.HashMap() ++ result
  }

  def main(args: Array[String]) {
    if (args.length != 4) {
      println("Usage: scala BFSApp [edgelist] [subset cardinality] [output filename] [# of servers]")
      return
    }
    val G = new UndirectedGraph(args(0), parallel = true)
    SingleSourceApp.calculate(G, args(1).toInt, shortestPath, args(2), args(3).toInt)
  }
}