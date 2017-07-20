package com.gelerion.kafka.exactly.once.semantic;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//if the record is stored in a database and not in Kafka, how will our consumer know where to start reading when
//it is assigned a partition? This is exactly what seek() can be used for
public class SaveOffsetToExternalSource {
    private static Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    static KafkaConsumer<String, String> consumer;

    public static void main(String[] args) {
        ImmutableMap<String, Object> kafkaProps = ImmutableMap.<String, Object>builder()
                .put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
                .put(ConsumerConfig.GROUP_ID_CONFIG, "CountryCounter")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .build();

        consumer = new KafkaConsumer<>(kafkaProps);

        consumer.subscribe(Collections.singletonList("customerCountries"), new SaveOffsetsOnRebalance());
        consumer.poll(0);

        for (TopicPartition partition : consumer.assignment()) {
            consumer.seek(partition, getOffsetFromDB(partition));
        }

/*        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
            {
                processRecord(record);
                storeRecordInDB(record);
                storeOffsetInDB(record.topic(), record.partition(), record.offset()); 4
            }

            //!!!
            commitDBTransaction();
        }*/
    }

    private static class SaveOffsetsOnRebalance implements ConsumerRebalanceListener {

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            commitDbTransaction();
        }

        private void commitDbTransaction() {

        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            for (TopicPartition partition : partitions) {
                consumer.seek(partition, getOffsetFromDB(partition));
            }
        }
    }

    private static long getOffsetFromDB(TopicPartition partition) {
        return 0;
    }
}
