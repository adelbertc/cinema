package cinema.crew

import akka.actor._
import akka.routing.FromConfig
import java.io.PrintWriter
import scala.collection.immutable.Map
import scala.collection.mutable.ListBuffer
import cinema.graph.Graph
import cinema.actor._

class SubsetSingleSourcePairwiseDirector(myGraph: Graph, metric: (Graph, Int) => Map[Int, Double]) extends Director {
  def receive = {
    case SingleSourceStartProduction(graphSlice) =>
      val pGraphSlice = graphSlice.par
      val results = for (u <- pGraphSlice) sender ! SingleSourcePairwiseResult(u, metric(myGraph, u))
  }
}

object SubsetSingleSourcePairwiseProducer {
  def props(myGraph: Graph, metric: (Graph, Int) => Map[Int, Double]) = Props(new SubsetSingleSourcePairwiseDirector(myGraph, metric)).withRouter(FromConfig())
}

class SubsetSingleSourcePairwiseProducer(myGraph: Graph, slices: List[Vector[Int]], metric: (Graph, Int) => Map[Int, Double], outputFilename: String) extends Producer {
  import SingleSourcePairwiseProducer.props
  val outfile = new PrintWriter(outputFilename)
  var counter = 0
  val graphdirectors = context.actorOf(props(myGraph, metric), name = "graphdirectors")
  val bound = slices.foldLeft(0)(_ + _.length)
  def receive = {
    case PreProduction => 
      slices.foreach(slice => graphdirectors ! SingleSourceStartProduction(slice))
    case SingleSourcePairwiseResult(u, value) => 
      value.foreach(t => outfile.println(u + " " + t._1 + " " + t._2))
      counter += 1
      if (counter == bound) {
        outfile.close()
        context.system.shutdown()
      }
  }
}