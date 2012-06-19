#Cinema
Cinema (work in progress) is a lightweight 
Scala library for efficient and concurrent
big graph processing.

The end goal is to have graph algorithms that 
are data-parallelizable easy to parallelize and/or 
distribute across several machines.

If your algorithm depends on the graph being
connected, please ensure beforehand that the
input graph is indeed connected before running.
There is currently no mechanism to check if a
graph is connected - I may add this feature
in the future.

Cinema includes one graph class (`UndirectedGraph`)
and runs on three `Actor` subclasses: `GraphProducer`,
`GraphDirector`, and `GraphGrip`. See below for
more details.

If a current "crew" does not suit your needs,
it is easy to implement one yourself - see the existing
crews for guidance.

Cinema is a work-in-progress and is not guarenteed
to be fully stable. Please report any bugs, comments,
or suggestions to me at: `adelbertc at gmail dot com`

An introductory tutorial on using Cinema can be found
[here](https://github.com/adelbertc/Cinema/tree/master/src/main/scala/cinema/example).

##Update (June 19, 2012)
I am currently working on a new iteration/successor of Cinema
with the general goal being to make trivially
parallelizable code even easier to distribute! Stay tuned..

##Compiling
Cinema requires Scala 2.9 and Akka 2.0. I suggest
downloading the [Typesafe Stack](http://typesafe.com/) and using the Simple
Build Tool (SBT) to compile Cinema.

Once it is compiled, make sure to make any
relevant changes to the configuration file in
`src/main/resources/application.conf`.

##Examples
There is an example application for calculating
hitting time (length of a random walk from
one vertex in G to another) in
`src/main/scala/example/hittingtime/`. A tutorial
on writing an application will be posted
soon in the Examples folder.

##Actors
The definitions of the Actors used are in the folder
`src/main/scala/cinema/actor/`. The definitions
inside `Actors.scala` are only abstract base classes
that extend Serializable (for remoting) and the
types of messages sent.

###Producer
The Producer gets the process started - it is 
created on the machine that will eventually 
aggregate all the results. Once the Producer 
is spawned by  the application and receives the 
`PreProduction` message, it in turn 
spawns (possibly several) Directors on remote 
machines. The Producer instructs the Directors to 
work by sending an `StartProduction` message to them. 
As the Director gets results, it forwards them to the
Producer, who will output the results to get
live updates on the computation.

###Director
Directors are spawned on remote machines by 
the Producer and are responsible for spawning
"worker" actors to do it's part of the computation.
Currently this is done via Scala 2.9's Parallel
Collections, but I am working on an alternative
involving "actual" Actors so that you can restrict
how many threads to use. A Director begins work when 
it receives an `StartProduction` message. As a
director gets results for it's slice of the graph,
it sends results to the Producer via a `ProductionResult`
message (see `src/main/scala/cinema/actor/Actors.scala`
for more details). 

###Grip
The Grips are deployed on the remote machines 
that will be used for computation. Their purpose 
is to provide an `ActorSystem` for the Producer 
to spawn Directors in.

Note that the Grips must be up and running before
the actual application can be run, as they provide
the `ActorSystem` in which the remote actors will
deploy. The Grips will not terminate themselves once 
the application is done running as they are deployed 
separately from the application, so remember to stop 
the process once you are done.

##Graphs in Cinema
The graph classes provided in Cinema is
`UndirectedGraph`. The class has both a
mutable and immutable version. 
Currently the immutable version only has a single 
constructor that takes in an edgelist file. I am 
working on expanding this to allow users to start
with a mutable graph and later on turn it into an
immutable graph.

##License
Copyright (c) 2012 Adelbert Chang

Licensed under the 
[MIT License](http://www.opensource.org/licenses/MIT).
Please refer to LICENSE for more details.
