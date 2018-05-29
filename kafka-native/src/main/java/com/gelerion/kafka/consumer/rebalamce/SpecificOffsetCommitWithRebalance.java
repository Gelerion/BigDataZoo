package com.gelerion.kafka.consumer.rebalamce;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by denis.shuvalov on 29/05/2018.
 */
public class SpecificOffsetCommitWithRebalance {

    private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private KafkaConsumer<String, String> consumer = consumer();
    private Collection<String> topics = Collections.singletonList("customerCountries");

    private class HandleRebalance implements ConsumerRebalanceListener {

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            System.out.println("Lost partitions in rebalance. Committing current offsets:" + currentOffsets);
            consumer.commitSync(currentOffsets);
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

        }
    }


    public void start() {
        try {
            consumer.subscribe(topics, new HandleRebalance());

            while (!Thread.currentThread().isInterrupted()) {

                for (ConsumerRecord<String, String> record : consumer.poll(100)) {
                    System.out.printf("topic = %s, partition = %s, offset = %d, customer = %s, country = %s\n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());

                    TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
                    OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(record.offset() + 1, "no metadata");
                    currentOffsets.put(topicPartition, offsetAndMetadata);
                }

                consumer.commitAsync(currentOffsets, null);
            }
        }
        catch (WakeupException e) {
            // ignore, we're closing
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                consumer.close();
                System.out.println("Closed consumer and we are done");
            }
        }
    }


    private KafkaConsumer<String, String> consumer() {
        ImmutableMap<String, Object> kafkaProps = ImmutableMap.<String, Object>builder()
                .put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") //List of host:port pairs of Kafka brokers
                .put(ConsumerConfig.GROUP_ID_CONFIG, "CountryCounter")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .build();

        return new KafkaConsumer<>(kafkaProps);
    }

}
