package com.gelerion.kafka.consumer.rebalamce;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * If you know your consumer is about to lose ownership of a partition, you will want to commit offsets of the last
 * event you’ve processed. If your consumer maintained a buffer with events that it only processes occasionally
 * (e.g., the currentRecords map we used when explaining pause() functionality), you will want to process the events
 * you accumulated before losing ownership of the partition. Perhaps you also need to close file handles, database
 * connections, and such.
 */
public class HandleRebalance implements ConsumerRebalanceListener {
    private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    //called before the rebalancing starts and after the consumer stopped consuming messages
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        System.out.println("Lost partitions in rebalance. Committing current offsets:" + currentOffsets);
        //consumer.commitSync(currentOffsets);
    }

    //called after partitions has been re-assigned to the broker, but before the consumer started consuming messages
    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

    }
}
