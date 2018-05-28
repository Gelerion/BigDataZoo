package com.gelerion.kafka.producer;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Created by denis.shuvalov on 15/05/2018.
 *
 * We send a message to the server and don’t really care if it arrives successfully or not. Most of the time, it will
 * arrive successfully, since Kafka is highly available and the producer will retry sending messages automatically.
 * However, some messages will get lost using this method.
 */
public class FireAndForgetProducer {

    public static void main(String[] args) {
        ImmutableMap<String, Object> kafkaProps = Producers.localBroker();

        KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProps);

        try {
            producer.send(new ProducerRecord<>("topic", "key", "value"));
        }
        catch (Exception e) {
            /*
            While we ignore errors that may occur while sending messages to Kafka brokers or in the brokers themselves,
            we may still get an exception if the producer encountered errors before sending the message to Kafka. Those
            can be a SerializationException when it fails to serialize the message, a BufferExhaustedException or TimeoutException
            if the buffer is full, or an InterruptException if the sending thread was interrupted.
             */
            e.printStackTrace();
        }
    }

}
