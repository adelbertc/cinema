package cinema.actor

import akka.actor._

abstract class SerializableActor extends Actor with Serializable
abstract class Director extends SerializableActor 
abstract class Producer extends SerializableActor
abstract class StuntPerformer extends SerializableActor

case object PreProduction
case class StartProduction(graphSlice: Vector[Int], subset: Vector[Int])
case class ProductionResult(u: Int, v: Int, result: Double)
case class ProbProductionResult(u: Int, v: Int, result: List[Int])