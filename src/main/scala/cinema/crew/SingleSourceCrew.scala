package cinema.crew

import akka.actor._
import akka.routing.FromConfig
import java.io.PrintWriter
import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer
import cinema.graph.Graph
import cinema.actor._

class SingleSourceDirector(myGraph: Graph, metric: (Graph, Int) => HashMap[Int, Int]) extends Director {
  def receive = {
    case StartProduction(graphSlice, subset) =>
      val pGraphSlice = graphSlice.par
      val results = for (u <- pGraphSlice) sender ! SingleSourceResult(u, metric(myGraph, u))
  }
}

object SingleSourceProducer {
  def props(myGraph: Graph, metric: (Graph, Int) => HashMap[Int, Int]) = Props(new SingleSourceDirector(myGraph, metric)).withRouter(FromConfig())
}

class SingleSourceProducer(myGraph: Graph, slices: List[Vector[Int]], subset: Vector[Int], metric: (Graph, Int) => HashMap[Int, Int], outputFilename: String) extends Producer {
  import SingleSourceProducer.props
  val outfile = new PrintWriter(outputFilename)
  var counter = 0
  val graphdirectors = context.actorOf(props(myGraph, metric), name = "graphdirectors")
  def receive = {
    case PreProduction => 
      slices.foreach(slice => graphdirectors ! StartProduction(slice, subset))
    case SingleSourceResult(u, value) => 
      value.foreach(t => outfile.println(u + " " + t._1 + " " + t._2))
      counter += 1
      if (counter == subset.length) {
        outfile.close()
        context.system.shutdown()
      }
  }
}