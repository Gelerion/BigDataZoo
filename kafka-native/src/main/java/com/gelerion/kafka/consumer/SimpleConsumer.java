package com.gelerion.kafka.consumer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;

//One consumer per thread is the rule
//Multithreaded example: https://www.confluent.io/blog/tutorial-getting-started-with-the-new-apache-kafka-0-9-consumer-client/
public class SimpleConsumer {

    //group.id - it specifies the Consumer Group the KafkaConsumer instance belongs to. While it is possible to
    //create consumers that do not belong to any consumer group, this is far less common
    public static void main(String[] args) {
        ImmutableMap<String, Object> kafkaProps = ImmutableMap.<String, Object>builder()
                .put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") //List of host:port pairs of Kafka brokers
                .put(ConsumerConfig.GROUP_ID_CONFIG, "CountryCounter")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .build();

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaProps);

        //It is also possible to call subscribe with a regular expression consumer.subscribe("test.*");
        consumer.subscribe(Collections.singletonList("customerCountries"));

        HashMap<String, String> customerCountryMap = Maps.newHashMap();

        //the poll loop
        try {
            //enable.auto.commit is set to true (which is the default) the consumer automatically triggers offset commits
            //periodically according to the interval configured with “auto.commit.interval.ms.”
            while (true) {
                //consumers must keep polling Kafka or they will be considered dead and the partitions
                //they are consuming will be handed to another consumer in the group to continue consuming
                //controls how long poll() will block if data is not available in the consumer buffer
                ConsumerRecords<String, String> records = consumer.poll(100);

                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(String.format(
                            "topic = %s, partition = %s, offset = %d, customer = %s, country = %s\n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value()));

                    customerCountryMap.put(record.key(), record.value());
                    JSONObject json = new JSONObject(customerCountryMap);
                    System.out.println(json.toString(1));
                    customerCountryMap.clear();
                }
            }
        }
        finally {
            //will close the network connections and the sockets and will trigger a rebalance immediately
            //rather than wait for the Group Coordinator to discover that the consumer stopped sending
            //heartbeats and is likely dead, which will take longer and therefore result in a longer period
            //of time during which no one consumes messages from a subset of the partitions
            consumer.close();
        }
    }
}
