package com.gelerion.kafka.rebalamce;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
