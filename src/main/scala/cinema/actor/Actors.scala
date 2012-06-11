package cinema.actor

import akka.actor._

abstract class SerializableActor extends Actor with Serializable
abstract class Director extends SerializableActor 
abstract class Producer extends SerializableActor
abstract class StuntPerformer extends SerializableActor

case object PreProduction
case class StartProduction(graphSlice: Vector[Int], subset: Vector[Int])
case class SingleSourceStartProduction(graphSlice: Vector[Int])

sealed trait ProductionResult
case class PairwiseResult(u: Int, v: Int, result: Double) extends ProductionResult
case class ProbPairwiseResult(u: Int, v: Int, result: List[Int]) extends ProductionResult
case class SingleSourceResult(u: Int, result: Double) extends ProductionResult
case class SingleSourcePairwiseResult(u: Int, result: scala.collection.immutable.Map[Int, Double]) extends ProductionResult