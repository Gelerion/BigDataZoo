package com.gelerion.kafka.consumer;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * Created by denis.shuvalov on 29/05/2018.
 *
 * Committing the latest offset only allows you to commit as often as you finish processing batches.
 * But what if you want to commit more frequently than that? What if poll() returns a huge batch and you want to commit
 * offsets in the middle of the batch to avoid having to process all those rows again if a rebalance occurs?
 * You can’t just call commitSync() or commitAsync()—this will commit the last offset returned, which you didn’t get to process yet.
 */
public class SpecificOffsetCommit {

    public static void main(String[] args) {

        KafkaConsumer<String, String> consumer = consumer();

        consumer.subscribe(Collections.singletonList("customerCountries"));

        Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
        int count = 0;

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);

                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(format("topic = %s, partition = %s, offset = %d, customer = %s, country = %s\n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value()));

                    //-----
                    TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
                    OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(record.offset() + 1, "no metadata");

                    //the consumer API allows you to call commitSync() and commitAsync() and pass a map of partitions and offsets that you wish to commit.
                    currentOffsets.put(topicPartition, offsetAndMetadata);

                    if (count % 1000 == 0) consumer.commitAsync(currentOffsets, null);
                    count++;
                }
            }
        }
        finally {
            consumer.close();
        }
    }

    private static KafkaConsumer<String, String> consumer() {
        ImmutableMap<String, Object> kafkaProps = ImmutableMap.<String, Object>builder()
                .put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") //List of host:port pairs of Kafka brokers
                .put(ConsumerConfig.GROUP_ID_CONFIG, "CountryCounter")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .build();

        return new KafkaConsumer<>(kafkaProps);
    }

}
