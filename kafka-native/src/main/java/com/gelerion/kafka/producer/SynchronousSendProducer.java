package com.gelerion.kafka.producer;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Created by denis.shuvalov on 27/05/2018.
 *
 * Here, we are using Future.get() to wait for a reply from Kafka. This method will throw an exception if the record
 * is not sent successfully to Kafka. If there were no errors, we will get a RecordMetadata object that we can use to
 * retrieve the offset the message was written to.
 */
public class SynchronousSendProducer {

    public static void main(String[] args) {
        ImmutableMap<String, Object> kafkaProps = Producers.localBroker();

        KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProps);

        try {
            producer.send(new ProducerRecord<>("topic", "key", "value"))
                    //wait for response
                    .get();
        }
        catch (Exception e) {
            //KafkaProducer has two types of errors. Retriable errors are those that can be resolved by sending the
            //message again. For example, a connection error can be resolved because the connection may get reestablished.
            //A “no leader” error can be resolved when a new leader is elected for the partition. KafkaProducer can be
            //configured to retry those errors automatically, so the application code will get retriable exceptions only
            //when the number of retries was exhausted and the error was not resolved. Some errors will not be resolved
            //by retrying. For example, “message size too large.” In those cases, KafkaProducer will not attempt a retry
            //and will return the exception immediately.
            e.printStackTrace();
        }
    }

}
