package cinema.example

import cinema.crew._
import cinema.graph.Graph
import cinema.graph.immutable.UndirectedGraph

object DegreeApp {
  def degree(myGraph: Graph, u: Int): Double = myGraph.degree(u)

  def main(args: Array[String]) {
    if (args.length != 4) {
      println("Usage: scala DegreeApp [edgelist] [subset cardinality] [output filename] [# of servers]")
      return
    }
    val G = new UndirectedGraph(args(0), parallel = true)
    SingleSourceApp.calculate(G, args(1).toInt, degree, args(2), args(3).toInt)
  }
}