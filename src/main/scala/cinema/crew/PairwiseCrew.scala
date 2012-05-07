package cinema.crew

import akka.actor._
import akka.routing.FromConfig
import java.io.PrintWriter
import scala.collection.mutable.ListBuffer
import cinema.graph.Graph
import cinema.actor._

class PairwiseDirector(myGraph: Graph, metric: (Graph, Int, Int) => Double) extends Director {
  def receive = {
    case StartProduction(graphSlice, subset) =>
      val pGraphSlice = graphSlice.par
      val results = for (u <- pGraphSlice; v <- subset) sender ! PairwiseResult(u, v, metric(myGraph, u, v))
  }
}

object PairwiseProducer {
  def props(myGraph: Graph, metric: (Graph, Int, Int) => Double) = Props(new PairwiseDirector(myGraph, metric)).withRouter(FromConfig())
}

class PairwiseProducer(myGraph: Graph, slices: List[Vector[Int]], subset: Vector[Int], metric: (Graph, Int, Int) => Double, outputFilename: String) extends Producer {
  import PairwiseProducer.props
  val outfile = new PrintWriter(outputFilename)
  var counter = 0
  val graphdirectors = context.actorOf(props(myGraph, metric), name = "graphdirectors")
  def receive = {
    case PreProduction => 
      slices.foreach(slice => graphdirectors ! StartProduction(slice, subset))
    case PairwiseResult(u, v, value) => 
      outfile.println(u + " " + v + " " + value)
      counter += 1
      if (counter == (subset.length * subset.length)) {
        outfile.close()
        context.system.shutdown()
      }
  }
}