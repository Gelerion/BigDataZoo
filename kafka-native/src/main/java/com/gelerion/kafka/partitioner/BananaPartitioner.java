package com.gelerion.kafka.partitioner;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.record.InvalidRecordException;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * Kafka does not limit you to just hash partitions, and sometimes there are good reasons to partition data differently.
 * For example, suppose that you are a B2B vendor and your biggest customer is a company that manufactures handheld
 * devices called Bananas. Suppose that you do so much business with customer “Banana” that over 10% of your daily
 * transactions are with this customer. If you use default hash partitioning, the Banana records will get allocated to
 * the same partition as other accounts, resulting in one partition being about twice as large as the rest.
 * This can cause servers to run out of space, processing to slow down, etc. What we really want is to give Banana its
 * own partition and then use hash partitioning to map the rest of the accounts to partitions.
 */
public class BananaPartitioner implements Partitioner {

    //we really should have passed the special customer name through configure instead of hard-coding it in partition
	@Override
	public void configure(Map<String, ?> configs) {}

	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
		int numPartitions = partitions.size();

		if ((keyBytes == null) || (!(key instanceof String)))
			throw new InvalidRecordException("We expect all messages to have customer name as key");

        if (key.equals("Banana"))
            return numPartitions; // Banana will always go to last partition

        // Other records will get hashed to the rest of the partitions
        return (Math.abs(Utils.murmur2(keyBytes)) % (numPartitions - 1));
    }

	@Override
	public void close() {

	}
}
