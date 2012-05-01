#Cinema Tutorial
The example code below can be found under
`cinema.example.hittingtime`.

##Instructions
This example will provide a short walkthrough on how
to distribute a hitting time calculation algorithm.
Hitting time is defined as the expected length of a random
walk from some vertex `u` to some other vertex `v` in
the graph. In this example, we calculate this by average
the lengths of 2000 such random walks.

* Create a new Scala program with a `main` method.

```scala
object HittingTimeApp {
  def main(args: Array[String])
}
```

* Import `scala.collection.mutable.ListBuffer`,
`import cinema.crew._` and
`import cinema.graph.immutable.UndirectedGraph` to
get the necessary objects and classes.

* Implement the algorithm as a method inside the object.
Note that `Graph` is an abstract class that serves to guarentee
all sub-classes have certain important methods. In this case,
we have guarenteed any `Graph` has a `randomNeighbor` method.
My using an abstract class as the parameter type, when we
create the actual graph we will be using, it could be
undirected, unweighted, directed, weighted, or any legal
combination of those, so long as they are a sub-class of
`Graph`.

```scala
def hittingTime(myGraph: Graph, u: Int, v: Int): List[Int] = {
  val result = new ListBuffer[Int]
  var i = 0
  while (i != 2000) {
    var walker = u
    var walkLength = 0
    while (walker != v) {
      walker = myGraph.randomNeighbor(walker)
      walkLength += 1
    }
    i += 1
    result += walkLength
  }
  ret.toList
}
```

* Inside the `main` method, call the `calculate` method 
inside the `ProbGraphApp` object (found in 
`cinema.crew`) with the arguments: 
`Graph`, number of vertices in graph to consider, 
the metric implementation, the output filename, and the 
number of servers to deploy over. Below is what the entire 
program should look like. We use `ProbGraphApp` since we
want a `List[Int]` of the length of each random walk since
random walks are non-deterministic.

```scala
package cinema.example.hittingtime

import cinema.crew._
import cinema.graph.immutable.UndirectedGraph

object HittingTimeApp {
  def hittingTime(myGraph: Graph, u: Int, v: Int): List[Int] = {
    val result = new ListBuffer[Int]
    var i = 0
    while (i != 2000) {
      var walker = u
      var walkLength = 0
      while (walker != v) {
        walker = myGraph.randomNeighbor(walker)
        walkLength += 1
      }
      i += 1
      result += walkLength
    }
    walkLength.toList
  }

  def main(args: Array[String]) {
    if (args.length != 4) {
      println("Usage: scala HittingTimeApplication [edgelist] [subset cardinality] [output filename] [# of servers]")
      return
    }
    val G = new UndirectedGraph(args(0), parallel = true)
    ProbGraphApp.calculate(G, args(1).toInt, hittingTime, args(2), args(3).toInt)
  }
}
```
    
* Edit the `application.conf` file found in `src/main/resources/` 
where the comments indicate. If you are deploying on a local machine 
via the loopback address, make sure to modify the `GraphGrip` program in 
`cinema.crew` to set the hostname to `127.0.0.1`

* Compile the project and run `GraphGrip`s on each machine you will be 
deploying to (I recommend using a script to automate this).

* Once each `GraphGrip` is successfully deployed, run the application and 
watch the CPU usage on each machine ramp up!

* Once you are done, remember to stop the `GraphGrip`
application on each machine - they will not stop
themselves as they are separate from the computation
process.