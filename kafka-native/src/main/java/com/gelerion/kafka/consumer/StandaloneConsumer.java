package com.gelerion.kafka.consumer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.util.List;

//Sometimes you know you have a single consumer that always needs to read data from all the partitions in a topic,
//or from a specific partition in a topic
public class StandaloneConsumer {
    public static void main(String[] args) {

        ImmutableMap<String, Object> kafkaProps = ImmutableMap.<String, Object>builder()
                .put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") //List of host:port pairs of Kafka brokers
                .put(ConsumerConfig.GROUP_ID_CONFIG, "CountryCounter")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .build();

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaProps);

        List<PartitionInfo> partitionInfos = consumer.partitionsFor("topic");
        List<TopicPartition> partitions = Lists.newArrayList();

        if (partitionInfos != null) {
            for (PartitionInfo partitionInfo : partitionInfos) {
                partitions.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
                consumer.assign(partitions);
            }
        }

        //... .poll() ...

    }
}
