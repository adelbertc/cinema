package cinema.crew.randomwalk

import akka.actor._
import akka.routing.FromConfig
import java.io.PrintWriter
import scala.collection.mutable.ListBuffer
import cinema.graph.immutable.RandomWalkGraph
import cinema.actor._

class RandomWalkDirector(myGraph: RandomWalkGraph, metric: (RandomWalkGraph, Int, Int) => Double, producer: ActorRef) extends Director {
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

object RandomWalkProducer {
  def props(myGraph: RandomWalkGraph, metric: (RandomWalkGraph, Int, Int) => Double, self: ActorRef) = Props(new RandomWalkDirector(myGraph, metric, self)).withRouter(FromConfig())
}

class RandomWalkProducer(myGraph: RandomWalkGraph, slices: List[Vector[Int]], subset: Vector[Int], metric: (RandomWalkGraph, Int, Int) => Double, outputFilename: String) extends Producer {
  import RandomWalkProducer.props
  val outfile = new PrintWriter(outputFilename)
  var counter = 0
  val htdirectors = context.actorOf(props(myGraph, metric, this.self), name = "rwdirectors")
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