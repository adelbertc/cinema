package cinema.example

import scala.collection.immutable
import scala.collection.mutable
import cinema.crew._
import cinema.graph.Graph
import cinema.graph.immutable.UndirectedGraph

object SubsetPPRApp {
  def PPR(myGraph: Graph, u: Int): immutable.Map[Int, Double] = {
    import akka.jsr166y.ThreadLocalRandom
    
    val result = new mutable.HashMap[Int, Double]
    myGraph.getVertices.foreach(vertex => result(vertex) = 0)
    
    var i = 0
    while (i != 1000) { // CHANGE
      var walker = u
      var hopsSoFar = 0
      
      while (hopsSoFar != 100) { // CHANGE
        if (ThreadLocalRandom.current.nextFloat > 0.1)
          walker = myGraph.randomNeighbor(walker)
        hopsSoFar += 1
      }
      result(walker) += 1
      i += 1
    }
    result.foreach(t => result(t._1) = (t._2 / 100)) // CHANGE
    result.toMap
  }

  def main(args: Array[String]) {
    if (args.length != 4) {
      println("Usage: scala SubsetPPRApp [edgelist] [subset cardinality] [output filename] [# of servers]")
      return
    }
    val G = new UndirectedGraph(args(0), parallel = true)
    SingleSourcePairwiseApp.calculate(G, args(1).toInt, PPR, args(2), args(3).toInt)
  }
}