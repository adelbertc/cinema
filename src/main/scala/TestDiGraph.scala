package cinema.test.digraph
import cinema.graph.mutable.DirectedGraph
import cinema.graph.mutable.UndirectedGraph

object DiGraph {
  def main(args: Array[String]) {
    val G = new UndirectedGraph
    G.addEdge(5, 4)
    G.addEdge(4, 5)
    println(G)
  }
}