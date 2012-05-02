package cinema.crew

import akka.actor._
import akka.routing.FromConfig
import java.io.PrintWriter
import scala.collection.mutable.ListBuffer
import cinema.graph.Graph
import cinema.actor._

class GraphDirector(myGraph: Graph, metric: (Graph, Int, Int) => Double) extends Director {
  def receive = {
    case StartProduction(graphSlice, subset) =>
      val pGraphSlice = graphSlice.par
      val results = for (u <- pGraphSlice; v <- subset) sender ! ProductionResult(u, v, metric(myGraph, u, v))
  }
}

object GraphProducer {
  def props(myGraph: Graph, metric: (Graph, Int, Int) => Double) = Props(new GraphDirector(myGraph, metric)).withRouter(FromConfig())
}

class GraphProducer(myGraph: Graph, slices: List[Vector[Int]], subset: Vector[Int], metric: (Graph, Int, Int) => Double, outputFilename: String) extends Producer {
  import GraphProducer.props
  val outfile = new PrintWriter(outputFilename)
  var counter = 0
  val graphdirectors = context.actorOf(props(myGraph, metric), name = "graphdirectors")
  def receive = {
    case PreProduction => 
      slices.foreach(slice => graphdirectors ! StartProduction(slice, subset))
    case ProductionResult(u, v, value) => 
      outfile.println(u + " " + v + " " + value)
      counter += 1
      if (counter == slices.length) {
        outfile.close()
        context.system.shutdown()
      }
  }
}