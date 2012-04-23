#Cinema
Cinema (work in progress) is a lightweight 
Scala library for efficient and concurrent
big graph processing.

The end goal is to have graph algorithms that 
are data-parallelizable easy to parallelize and/or 
distribute across several machines.

Cinema includes two graph classes (`UndirectedGraph` 
and `RandomWalkGraph`) and runs on three Actors 
(`Producer`, `Director`, and `Grip`). 

Currently, only algorithms that take in pairs of 
vertices and return a floating point or integer 
value work (i.e. shortest path). This is because most 
of the algorithms I have dealt with thus far have 
this property and hence are easily parallelized. 
If you would like more, please let me know and I'll 
see what I can do. Or better yet, fork Cinema and add 
it yourself!

Cinema is a work-in-progress and is not guarenteed
to be fully stable. Please report any bugs, comments,
or suggestions to me at: `adelbertc at gmail dot com`

An introductory tutorial on using Cinema can be found
[here](https://github.com/adelbertc/Cinema/tree/master/src/main/scala/cinema/example).

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
When the Directors are done working on their slice of the 
graph, they will send an `ProductionResult` message to 
the Producer. The Producer will then write the 
results to an output file.

###Director
Directors are spawned on remote machines by 
the Producer and are responsible for spawning
"worker" actors to do it's part of the computation.
Currently this is done via Scala 2.9's Parallel
Collections, but I am working on an alternative
involving "actual" Actors so that you can restrict
how many threads to use. A Director begins work when 
it receives an `StartProduction` message. Once a 
Director is done with it's slice of the graph, 
it sends the results back to the Producer via an 
`ProductionResult` message.

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
The graph classes provided in Cinema are 
`UndirectedGraph` and `RandomWalkGraph`. The latter 
is an extension of the former. Futhermore, both 
classes have an immutable and mutable version. 
Currently the immutable versions only have a single 
constructor that takes in an edgelist file. I am 
working on expanding this to allow users to start
with a mutable graph and later on turn it into an
immutable graph.

##License
Copyright (c) 2012 Adelbert Chang

Licensed under the 
[MIT License](http://www.opensource.org/licenses/MIT).
Please refer to LICENSE for more details.