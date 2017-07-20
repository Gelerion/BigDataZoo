package com.gelerion.kafka.exactly.once.semantic;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * there is still a chance that our application will crash after the record was
 * stored in the database but before we committed offsets, causing the record
 * to be processed again and the database to contain duplicates
 *
 * ! This could be avoided if there was only a way to store both the record and the offset in one atomic action.
 *  As long as the records are written to a database and the offsets to Kafka, this is impossible
 *  But what if we wrote both the record and the offset to the database, in one transaction?
 */
public class ParanoidSolution {
    private static Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public static void main(String[] args) {
        ImmutableMap<String, Object> kafkaProps = ImmutableMap.<String, Object>builder()
                .put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") //List of host:port pairs of Kafka brokers
                .put(ConsumerConfig.GROUP_ID_CONFIG, "CountryCounter")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .build();

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaProps);

        consumer.subscribe(Collections.singletonList("customerCountries"));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset()));

                //some pre-clean up
                processRecord(record);
                storeRecordInDb(record);

                //we commit offsets after processing each record
                consumer.commitSync(currentOffsets);
            }
        }

    }

    private static void storeRecordInDb(ConsumerRecord<String, String> record) {
        //No-op
    }

    private static void processRecord(ConsumerRecord<String, String> record) {
        //No-op
    }
}
