If you want to start reading all messages from the beginning of the partition, or you want to
skip all the way to the end of the partition and start consuming only new messages, there are
APIs specifically for that: seekToBeginning(TopicPartition tp) and seekToEnd(TopicPartition tp).

However, the Kafka API also lets you seek to a specific offset. This ability can be used in a
variety of ways, for example to go back few messages or skip ahead few messages (perhaps a
time-sensitive application that is falling behind will want to skip ahead to more relevant
messages), but the most exciting use-case for this ability is when offsets are stored in a
system other than Kafka