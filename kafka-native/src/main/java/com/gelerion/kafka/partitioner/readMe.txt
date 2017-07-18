When the key is null and the default partitioner is used, the record will be sent to one of
the available partitions of the topic at random. Round-robin algorithm will be used to balance
the messages between the partitions

If a key exists and the default partitioner is used, Kafka will hash the key (using its own hash algorithm,
so hash values will not change when Java is upgraded), and use the result to map the message to a specific partition.

The mapping of keys to partitions is consistent only as long as
the number of partitions in a topic does not change.