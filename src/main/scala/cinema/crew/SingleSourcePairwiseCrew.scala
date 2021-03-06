package cinema.crew

import akka.actor._
import akka.routing.FromConfig
import java.io.PrintWriter
import scala.collection.immutable.Map
import scala.collection.mutable.ListBuffer
import cinema.graph.Graph
import cinema.actor._

class SingleSourcePairwiseDirector(myGraph: Graph, metric: (Graph, Int) => Map[Int, Double]) extends Director {
  def receive = {
    case StartProduction(graphSlice, subset) =>
      val pGraphSlice = graphSlice.par
      val results = for (u <- pGraphSlice) sender ! SingleSourcePairwiseResult(u, metric(myGraph, u))
  }
}

object SingleSourcePairwiseProducer {
  def props(myGraph: Graph, metric: (Graph, Int) => Map[Int, Double]) = Props(new SingleSourcePairwiseDirector(myGraph, metric)).withRouter(FromConfig())
}

class SingleSourcePairwiseProducer(myGraph: Graph, slices: List[Vector[Int]], subset: Vector[Int], metric: (Graph, Int) => Map[Int, Double], outputFilename: String) extends Producer {
  import SingleSourcePairwiseProducer.props
  val outfile = new PrintWriter(outputFilename)
  var counter = 0
  val graphdirectors = context.actorOf(props(myGraph, metric), name = "graphdirectors")
  def receive = {
    case PreProduction => 
      slices.foreach(slice => graphdirectors ! StartProduction(slice, subset))
    case SingleSourcePairwiseResult(u, value) => 
      value.foreach(t => outfile.println(u + " " + t._1 + " " + t._2))
      counter += 1
      if (counter == subset.length) {
        outfile.close()
        context.system.shutdown()
      }
  }
}