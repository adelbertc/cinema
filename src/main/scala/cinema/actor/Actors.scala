package cinema.actor

import akka.actor._
import cinema.graph.Graph
import cinema.graph.immutable.RandomWalkGraph

abstract class SerializableActor extends Actor with Serializable
abstract class Director extends SerializableActor 
abstract class Producer extends SerializableActor
abstract class StuntPerformer extends SerializableActor

case object PreProduction
case class StartProduction(graphSlice: Vector[Int], subset: Vector[Int])
case class ProductionResult(results: List[Tuple2[Tuple2[Int, Int], Double]])