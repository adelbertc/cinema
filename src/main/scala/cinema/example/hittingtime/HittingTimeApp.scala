package cinema.example.hittingtime

import cinema.crew.randomwalk._
import cinema.graph.immutable.RandomWalkGraph

object HittingTimeApp {
  def hittingTime(myGraph: RandomWalkGraph, u: Int, v: Int): Double = {
    var ret = 0
    var i = 0
    while (i != RandomWalkGraph.RWLOOP) {
      var walker = u
      while (walker != v) {
        walker = myGraph.randomNeighbor(walker)
        ret += 1
      }
      i += 1
    }
    ret / RandomWalkGraph.RWLOOP
  }
  
  def main(args: Array[String]) {
    if (args.length != 4) {
      println("Usage: scala HittingTimeApplication <edgelist> <subset cardinality> <output filename> <# of servers>")
      return
    }
    val G = new RandomWalkGraph(args(0), parallel = true)
    RandomWalkApp.calculate(G, args(1).toInt, hittingTime, args(2), args(3).toInt)
  }
}