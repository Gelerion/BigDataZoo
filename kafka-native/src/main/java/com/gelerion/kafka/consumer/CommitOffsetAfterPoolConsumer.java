package com.gelerion.kafka.consumer;

import java.util.Collections;
import java.util.HashMap;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONObject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

//allows consumers to use Kafka to track their position (offset) in each partition
public class CommitOffsetAfterPoolConsumer {

    //How does a consumer commits an offset?
    // It produces a message to Kafka, to a special __consumer_offsets topic, with the committed offset for each partition.
    // If a consumer crashes or a new consumer joins the consumer group, this will trigger a rebalance
    // After a rebalance, each consumer may be assigned a new set of partitions than the one it processed before.
    // In order to know where to pick up the work, the consumer will read the latest committed offset of each partition and continue from there
    public static void main(String[] args) {

        ImmutableMap<String, Object> kafkaProps = ImmutableMap.<String, Object>builder()
                .put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") //List of host:port pairs of Kafka brokers
                .put(ConsumerConfig.GROUP_ID_CONFIG, "CountryCounter")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .build();

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaProps);

        consumer.subscribe(Collections.singletonList("customerCountries"));

        HashMap<String, String> customerCountryMap = Maps.newHashMap();

        // -------------------------------------------------------------------------------------------------------------
        // !! If the committed offset is smaller than the offset of the last message the client processed, the
        //    messages between the last processed offset and the committed offset will be processed twice

        // !! If the committed offset is larger than the offset of the last message the client actually processed,
        //    all messages between the last processed offset and the committed offset will be missed by the consumer group.
        // -------------------------------------------------------------------------------------------------------------

        try {
            while (true) {
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

                //after data being processed
                try {
                    //commit the last offset in the batch, before polling for additional messages
                    consumer.commitSync(); //retries committing as long as there is no error that can’t be recovered

                    //drawback of manual commit is that the application is blocked until the broker responds to the commit
                    //request. This will limit the throughput of the application
                    //Another option is the asynchronous commit API

                    //consumer.commitAsync(); async the good and the bad...

/*                    It is common to use the callback to log commit errors or to count them in a metric, but if you want
                      to use the callback for retries, you need to be aware of the problem with commit order.
                      consumer.commitAsync(new OffsetCommitCallback() {
                        public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception e) {
                            if (e != null)
                                System.err.println("Commit failed for offsets" + offsets + " " + e);
                        }
                    });*/
                }
                catch (CommitFailedException e) {
                    System.err.println("commit failed" + e);
                }
            }
        }
        finally {
            consumer.close();
        }
    }

}
