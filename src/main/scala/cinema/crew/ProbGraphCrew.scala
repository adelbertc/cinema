package cinema.crew

import akka.actor._
import akka.routing.FromConfig
import java.io.PrintWriter
import scala.collection.mutable.ListBuffer
import cinema.graph.Graph
import cinema.actor._

class ProbGraphDirector(myGraph: Graph, metric: (Graph, Int, Int) => List[Int]) extends Director {
  def receive = {
    case StartProduction(graphSlice, subset) =>
      val pGraphSlice = graphSlice.par
      val results = for (u <- pGraphSlice; v <- subset) sender ! ProbProductionResult(u, v, metric(myGraph, u, v))
  }
}

object ProbGraphProducer {
  def props(myGraph: Graph, metric: (Graph, Int, Int) => List[Int]) = Props(new ProbGraphDirector(myGraph, metric)).withRouter(FromConfig())
}

class ProbGraphProducer(myGraph: Graph, slices: List[Vector[Int]], subset: Vector[Int], metric: (Graph, Int, Int) => List[Int], outputFilename: String) extends Producer {
  import ProbGraphProducer.props
  val outfile = new PrintWriter(outputFilename)
  var counter = 0
  val graphdirectors = context.actorOf(props(myGraph, metric), name = "graphdirectors")
  def receive = {
    case PreProduction => 
      slices.foreach(slice => graphdirectors ! StartProduction(slice, subset))
    case ProbProductionResult(u, v, value) => 
      outfile.print(u + " " + v)
      value.foreach(value => outfile.print(" " + value))
      outfile.println()
      counter += 1
      if (counter == (subset.length * subset.length)) {
        outfile.close()
        context.system.shutdown()
      }
  }
}