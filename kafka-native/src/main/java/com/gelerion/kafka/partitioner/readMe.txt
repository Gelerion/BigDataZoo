When the key is null and the default partitioner is used, the record will be sent to one of
the available partitions of the topic at random. Round-robin algorithm will be used to balance
the messages between the partitions

If a key exists and the default partitioner is used, Kafka will hash the key (using its own hash algorithm,
so hash values will not change when Java is upgraded), and use the result to map the message to a specific partition.

The mapping of keys to partitions is consistent only as long as
the number of partitions in a topic does not change.

So as long as the number of partitions is constant, you can be sure that, for example, records regarding user 045189
will always get written to partition 34. This allows all kinds of optimization when reading data from partitions.
However, the moment you add new partitions to the topic, this is no longer guaranteed—the old records will stay in
partition 34 while new records will get written to a different partition. When partitioning keys is important, the
easiest solution is to create topics with sufficient partitions and never add partitions.