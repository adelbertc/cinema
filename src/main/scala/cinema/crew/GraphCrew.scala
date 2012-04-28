package cinema.crew

import akka.actor._
import akka.routing.FromConfig
import java.io.PrintWriter
import scala.collection.mutable.ListBuffer
import cinema.graph.Graph
import cinema.actor._

class GraphDirector(myGraph: Graph, metric: (Graph, Int, Int) => Double, producer: ActorRef) extends Director {
  val results = new ListBuffer[Tuple2[Tuple2[Int, Int], Double]]
  var resultBound = 0

  def receive = {
    case StartProduction(graphSlice, subset) =>
      val pGraphSlice = graphSlice.par
      val results = for {
        u <- pGraphSlice
        v <- subset
      } yield {
        ((u -> v) -> metric(myGraph, u, v))
      }
      sender ! ProductionResult(results.toList)
  }
}

object GraphProducer {
  def props(myGraph: Graph, metric: (Graph, Int, Int) => Double, self: ActorRef) = Props(new GraphDirector(myGraph, metric, self)).withRouter(FromConfig())
}

class GraphProducer(myGraph: Graph, slices: List[Vector[Int]], subset: Vector[Int], metric: (Graph, Int, Int) => Double, outputFilename: String) extends Producer {
  import GraphProducer.props
  val outfile = new PrintWriter(outputFilename)
  var counter = 0
  val htdirectors = context.actorOf(props(myGraph, metric, this.self), name = "graphdirectors")
  def receive = {
    case PreProduction => 
      slices.foreach(slice => htdirectors ! StartProduction(slice, subset))
    case ProductionResult(results) =>
      results.foreach(t => outfile.println(t._1._1 + " " + t._1._2 + " " + t._2))
      counter += 1
      println("Got results for " + counter + "th slice, " + (slices.length - counter) + " remaining...")
      if (counter == slices.length) {
        outfile.close()
        context.system.shutdown()
      }
  }
}