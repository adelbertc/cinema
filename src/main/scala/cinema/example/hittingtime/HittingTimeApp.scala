package cinema.example.hittingtime

import cinema.crew._
import cinema.graph.Graph
import cinema.graph.immutable.UndirectedGraph

object HittingTimeApp {
  def hittingTime(myGraph: Graph, u: Int, v: Int): Double = {
    var ret = 0
    var i = 0
    while (i != 2000) {
      var walker = u
      while (walker != v) {
        walker = myGraph.randomNeighbor(walker)
        ret += 1
      }
      i += 1
    }
    ret / 2000
  }
  
  def main(args: Array[String]) {
    if (args.length != 4) {
      println("Usage: scala HittingTimeApplication <edgelist> <subset cardinality> <output filename> <# of servers>")
      return
    }
    val G = new UndirectedGraph(args(0), parallel = true)
    GraphApp.calculate(G, args(1).toInt, hittingTime, args(2), args(3).toInt)
  }
}