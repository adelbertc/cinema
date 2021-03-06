package cinema.example

import scala.collection.mutable.ListBuffer
import cinema.crew._
import cinema.graph.Graph
import cinema.graph.immutable.UndirectedGraph

object HittingTimeApp {
  def hittingTime(myGraph: Graph, u: Int, v: Int): List[Int] = {
    val result = new ListBuffer[Int]    
    var i = 0
    while (i != 2000) {
      var walker = u
      var walkLength = 0
      while (walker != v) {
        walker = myGraph.randomNeighbor(walker)
        walkLength += 1
      }
      i += 1
      result += walkLength
    }
    result.toList
  }
  
  def main(args: Array[String]) {
    if (args.length != 4) {
      println("Usage: scala HittingTimeApp [edgelist] [subset cardinality] [output filename] [# of servers]")
      return
    }
    val G = new UndirectedGraph(args(0), parallel = true)
    ProbPairwiseApp.calculate(G, args(1).toInt, hittingTime, args(2), args(3).toInt)
  }
}