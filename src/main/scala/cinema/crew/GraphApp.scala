package cinema.crew

import akka.actor._
import scala.collection.mutable.ListBuffer
import cinema.actor._
import cinema.graph.Graph

object GraphApp {
  def calculate(myGraph: Graph, k: Int, metric: (Graph, Int, Int) => Double, outputFilename: String, numOfServers: Int) {
    val system = ActorSystem("GraphApplication")
    val vertexSubset = myGraph.getRandomVertices(k)

    val slices = new ListBuffer[Vector[Int]]
    val stepSize = k / numOfServers
    var currentStep = 0
    var looper = 0
    while (looper != numOfServers) {
      if (looper != numOfServers - 1) {
        slices += vertexSubset.slice(currentStep, currentStep + stepSize)
        currentStep += stepSize
      } else {
        slices += vertexSubset.slice(currentStep, vertexSubset.length)
      }
      looper += 1
    }
    val producer = system.actorOf(Props(new GraphProducer(myGraph, slices.toList, vertexSubset, metric, outputFilename)), name = "graphproducer")
    println("Starting computation...")
    producer ! PreProduction
  }
}