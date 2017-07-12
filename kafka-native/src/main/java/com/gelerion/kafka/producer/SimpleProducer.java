package com.gelerion.kafka.producer;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class SimpleProducer {

    public static void main(String[] args) {
        ImmutableMap<String, Object> kafkaProps = ImmutableMap.<String, Object>builder()
                .put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") //List of host:port pairs of Kafka brokers
                .put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
                .put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
                .build();

        KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProps);

        //There are three primary methods of sending messages:
        // - Fire-and-forget
        // - Synchronous Send
        // - Asynchronous Send

        ProducerRecord<String, String> record =
                new ProducerRecord<>("CustomerCountry", "Precision Products", "France");

        try {
            //the message will be placed in a buffer and will be sent to the broker in a separate thread
            producer.send(record);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
