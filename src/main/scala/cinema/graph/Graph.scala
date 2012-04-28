package cinema.graph

abstract class Graph extends Serializable {
  def numberOfVertices: Int
  def hasVertex(u: Int): Boolean
  def getVertices: Vector[Int]
  
  def numberOfEdges: Int
  def hasEdge(u: Int, v: Int): Boolean
  def getEdges: Vector[Tuple2[Int, Int]]
  
  def degree(u: Int): Int
  def neighbors(u: Int): Vector[Int]
  def averageDegree: Int
  def slice(k: Int): Vector[Vector[Int]]
  def randomNeighbor(u: Int): Int
  def getRandomVertices(k: Int): Vector[Int]
  
  override def toString(): String = 
    "Number of vertices: " + numberOfVertices + "\nNumber of edges: " + numberOfEdges
}