#Cinema
Cinema (work in progress) is a lightweight Scala framework for efficient and concurrent
big graph processing.

The end goal is to have graph algorithms that are data-parallelizable easy to distribute.
Users should be able to implement a graph algorithm, pass the graph and algorithm to
a class, and have it be parallelized either locally or distributed, as specified in the
configuration file.
