package cinema.example.hittingtime

import akka.actor._
import scala.collection.mutable.ListBuffer
import cinema.actor._
import cinema.crew.randomwalk._
import cinema.graph.immutable.RandomWalkGraph

object HittingTimeApplication {
  def hittingTime(myGraph: RandomWalkGraph, u: Int, v: Int): Double = {
    var ret = 0
    var i = 0
    while (i != RandomWalkGraph.RWLOOP) {
      var walker = u
      while (walker != v) {
        walker = myGraph.randomNeighbor(walker)
        ret += 1
      }
      i += 1
    }
    ret / RandomWalkGraph.RWLOOP
  }

  def main(args: Array[String]) {
    if (args.length != 4) {
      println("Usage: scala HittingTimeApplication <edgelist> <subset cardinality> <output filename> <# of servers>")
      return
    }

    val myGraph = new RandomWalkGraph(args(0), parallel = true)
    val k = args(1).toInt
    val outputFilename = args(2)
    val numOfServers = args(3).toInt

    val system = ActorSystem("HittingTimeApplication")
    val vertexSubset = myGraph.getRandomVertices(k)

    val slices = new ListBuffer[Vector[Int]]
    val stepSize = k / numOfServers
    //println("Step size: " + stepSize)
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
    //slices.foreach(slice => println(slice))
    val producer = system.actorOf(Props(new RandomWalkProducer(myGraph, slices.toList, vertexSubset, hittingTime, outputFilename)), name = "htproducer")
    println("Starting computation...")
    producer ! PreProduce
  }
}