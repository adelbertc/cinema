#Cinema Tutorial
The example code below can be found under
`cinema.example.hittingtime`. Currently
there is only a `Crew` for `immutable.RandomWalkGraph`.
I will be adding a `Crew` for `immutable.UndirectedGraph`
soon.

##Instructions
1. Create a new Scala program with a `main` method.

```scala
object HittingTimeApp {
  def main(args: Array[String])
}
```

2. Import `import cinema.crew.randomwalk._` and
`import cinema.graph.immutable.RandomWalkGraph` to
get the necessary objects and classes.

3. Implement the algorithm as a method inside the object.

```scala
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
```
    
4. Inside the `main` method, call the `calculate` method 
inside the `RandomWalkApp` object (found in 
`cinema.crew.randomwalk`) with the arguments: 
`RandomWalkGraph`, number of vertices in graph to consider, 
the metric implementation, the output filename, and the 
number of servers to deploy over. Below is what the entire 
program should look like.

```scala
package cinema.example.hittingtime

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
    val G = new RandomWalkGraph(args(0), parallel = true)
    RandomWalkApp.calculate(G, args(1).toInt, hittingTime, args(2), args(3).toInt)
  }
}
```
    
5. Edit the `application.conf` file found in `src/main/resources/` 
where the comments indicate. If you are deploying on a local machine 
via the loopback address, make sure to modify the `Grip` program in 
`cinema.crew` to set the hostname to `127.0.0.1`

6. Compile the project and run `Grip`s on each machine you will be 
deploying to (I recommend using a script to automate this).

7. Once each Grip is successfully deployed, run the application and 
watch the CPU usage on each machine ramp up!

8. Once you are done, remember to stop the `Grip`
application on each machine - they will not stop
themselves as they are separate from the computation
process.