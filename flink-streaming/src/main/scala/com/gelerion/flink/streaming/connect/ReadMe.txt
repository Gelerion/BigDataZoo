CONNECT, COMAP, AND COFLATMAP [CONNECTEDSTREAMS -> DATASTREAM]
Sometimes it is necessary to associate two input streams that are not of the same type. A very common requirement is
to join events of two streams. A more concrete example is an application that computes real-time online offers based
on a set of evolving factors. Such factors can include inventory availability, marketing campaigns, current popularity
of an item, or social media sentiment analysis insights. The application receives two input streams; the first one is
a stream of factor indicators that influence prices and the second stream contains price requests from potential
customers. In order to modify prices based on the evolving indicators, the application has to share state between the
two streams.

The DataStream API provides the connect transformation to support such use-cases. The DataStream.connect() method
receives a DataStream and returns a ConnectedStreams object, which represents the two connected streams.

    // first stream
    val first: DataStream[Int] = ...
    // second stream
    val second: DataStream[String] = ...

    // connect streams
    val connected: ConnectedStreams[Int, String] = first.connect(second)

Please note that it is not possible to control the order in which the methods of CoMapFunction and CoFlatMapFunction
are called. Instead a method is  called as soon as an event arrived via the corresponding input.

Joint processing of two streams usually requires that events of both streams are deterministically routed based on some
condition to be processed by the same parallel instance of an operator. By default, connect() does not establish a
relationship between the events of both streams such that the events of both streams are randomly assigned to operator
instances. This behavior yields non-deterministic results and is usually not desired. In order to achieve deterministic
transformations on ConnectedStreams , connect() can be combined with keyBy() or broadcast() as follows:

    // first stream
    val first: DataStream[(Int, Long)] = ...
    // second stream
    val second: DataStream[(Int, String)] = ...

    // connect streams with keyBy
    val keyedConnect: ConnectedStreams[(Int, Long), (Int, String)] = first
      .connect(second)
      .keyBy(0, 0) // key both input streams on first attribute

    // connect streams with broadcast
    val keyedConnect: ConnectedStreams[(Int, Long), (Int, String)] = first
      .connect(second.broadcast()) // broadcast second input stream

Using keyBy() with connect() will route all events from both streams with the same key to the same operator instance.
An operator that is applied on a connected and keyed stream has access to keyed state 1. All events of a stream,
which is broadcasted before it is connected with another stream, are replicated and sent to all parallel operator
instances. Hence, all elements of both input streams can be jointly processed. In fact, the combinations of connect()
with keyBy() and broadcast() resemble the two most common shipping strategies for distributed joins:
repartition-repartition and broadcast-forward.