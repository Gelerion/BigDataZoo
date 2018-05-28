package com.gelerion.kafka.producer;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

public class SimpleProducer {

    public static void main(String[] args) {
        ImmutableMap<String, Object> kafkaProps = ImmutableMap.<String, Object>builder()
                //This list doesn’t need to include all brokers, since the producer will get more information after the initial
                //connection. But it is recommended to include at least two, so in case one broker goes down, the producer
                //will still be able to connect to the cluster.
                .put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") //List of host:port pairs of Kafka brokers
                .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
                .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
                .build();

        KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProps);

        //There are three primary methods of sending messages:
        // - Fire-and-forget
        // - Synchronous Send
        // - Asynchronous Send

        ProducerRecord<String, String> record =
                new ProducerRecord<>("customerCountries", "Precision Products", "France");

        try {
            //the message will be placed in a buffer and will be sent to the broker in a separate thread
            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get();
            System.out.println(metadata);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
