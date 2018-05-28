package com.gelerion.kafka.producer;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Created by denis.shuvalov on 27/05/2018.
 * <p>
 * Suppose the network roundtrip time between our application and the Kafka cluster is 10ms. If we wait for a reply after
 * sending each message, sending 100 messages will take around 1 second. On the other hand, if we just send all our messages
 * and not wait for any replies, then sending 100 messages will barely take any time at all. In most cases, we really don’t
 * need a reply—Kafka sends back the topic, partition, and offset of the record after it was written, which is usually not
 * required by the sending app. On the other hand, we do need to know when we failed to send a message completely so we
 * can throw an exception, log an error, or perhaps write the message to an “errors” file for later analysis.
 * <p>
 * In order to send messages asynchronously and still handle error scenarios, the producer supports adding a callback
 * when sending a record.
 */
public class AsynchronousSendProducer {

    public static void main(String[] args) {
        ImmutableMap<String, Object> kafkaProps = Producers.localBroker();

        KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProps);

        producer.send(new ProducerRecord<>("topic", "key", "value"), (metadata, ex) -> {
            if (ex != null) ex.printStackTrace();
        });

    }

}
