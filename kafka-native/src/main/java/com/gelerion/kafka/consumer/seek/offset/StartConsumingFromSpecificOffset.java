package com.gelerion.kafka.consumer.seek.offset;

/**
 * Created by denis.shuvalov on 30/05/2018.
 */
public class StartConsumingFromSpecificOffset {
    /*
    So far we’ve seen how to use poll() to start consuming messages from the last committed offset in each partition and
    to proceed in processing all messages in sequence. However, sometimes you want to start reading at a different offset.

    If you want to start reading all messages from the beginning of the partition, or you want to skip all the way to
    the end of the partition and start consuming only new messages, there are APIs specifically for that:
    seekToBeginning(TopicPartition tp) and seekToEnd(TopicPartition tp).

    However, the Kafka API also lets you seek a specific offset. This ability can be used in a variety of ways; for
    example, to go back a few messages or skip ahead a few messages (perhaps a time-sensitive application that is
    falling behind will want to skip ahead to more relevant messages). The most exciting use case for this ability
    is when offsets are stored in a system other than Kafka.
     */
}
