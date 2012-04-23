package cinema.crew.randomwalk

import akka.actor._
import scala.collection.mutable.ListBuffer
import cinema.actor._
import cinema.crew.randomwalk._
import cinema.graph.immutable.RandomWalkGraph

object RandomWalkApp {
  def calculate(myGraph: RandomWalkGraph, k: Int, metric: (RandomWalkGraph, Int, Int) => Double, outputFilename: String, numOfServers: Int) {
    val system = ActorSystem("RandomWalkApplication")
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
    val producer = system.actorOf(Props(new RandomWalkProducer(myGraph, slices.toList, vertexSubset, metric, outputFilename)), name = "rwproducer")
    println("Starting computation...")
    producer ! PreProduction
  }
}