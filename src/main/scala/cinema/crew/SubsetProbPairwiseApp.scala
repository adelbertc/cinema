package cinema.crew

import akka.actor._
import scala.collection.mutable.ListBuffer
import cinema.actor._
import cinema.graph.Graph

object SubsetProbPairwiseApp {
  def calculate(myGraph: Graph, k: Int, metric: (Graph, Int, Int) => List[Int], outputFilename: String, numOfServers: Int) {
    val system = ActorSystem("SubsetProbPairwiseApp")
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
    val producer = system.actorOf(Props(new SubsetProbPairwiseProducer(myGraph, slices.toList, myGraph.getVertices, metric, outputFilename)), name = "graphproducer")
    println("Starting computation...")
    producer ! PreProduction
  }
}