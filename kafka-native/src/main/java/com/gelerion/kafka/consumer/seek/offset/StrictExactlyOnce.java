package com.gelerion.kafka.consumer.seek.offset;

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
 * Created by denis.shuvalov on 30/05/2018.
 *
 * Think about this common scenario: Your application is reading events from Kafka (perhaps a clickstream of users
 * in a website), processes the data (perhaps remove records that indicate clicks from automated programs rather than users),
 * and then stores the results in a database, NoSQL store, or Hadoop. Suppose that we really don’t want to lose any data,
 * nor do we want to store the same results in the database twice.
 */
public class StrictExactlyOnce {
    private KafkaConsumer<String, String> consumer = consumer();
    private Collection<String> topics = Collections.singletonList("topic");
    private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public void start() {
        try {
            consumer.subscribe(topics, new SaveOffsetsOnRebalance());
            consumer.poll(0);

            for (TopicPartition partition : consumer.assignment()) {
                //When the consumer first starts, after we subscribe to topics, we call poll() once to make sure we join
                //a consumer group and get assigned partitions, and then we immediately seek() to the correct offset in
                //the partitions we are assigned to. Keep in mind that seek() only updates the position we are consuming
                //from, so the next poll() will fetch the right messages. If there was an error in seek()
                //(e.g., the offset does not exist), the exception will be thrown by poll().
                consumer.seek(partition, getOffsetFromDB(partition));
            }

            while (!Thread.currentThread().isInterrupted()) {

                for (ConsumerRecord<String, String> record : consumer.poll(100)) {
                    updateCurrentOffsets(record);

                    //---
                    processRecord(record);
                    storeRecordInDB(record);
                    consumer.commitAsync(currentOffsets, null);
                    //------------------
                    /*
                    It may look like:
                    while (true) {
                        ConsumerRecords<String, String> records = consumer.poll(100);
                        for (ConsumerRecord<String, String> record : records)
                        {
                            updateCurrentOffsets(record);
                            processRecord(record);
                            storeRecordInDB(record);
                            consumer.commitAsync(currentOffsets);
                        }
                    }
                    In this example, we are very paranoid, so we commit offsets after processing each record. However,
                    there is still a chance that our application will crash after the record was stored in the database
                    but before we committed offsets, causing the record to be processed again and the database to
                    contain duplicates.

                    But what if we wrote both the record and the offset to the database, in one transaction? Then we’ll
                    know that either we are done with the record and the offset is committed or we are not and the record
                    will be reprocessed.

                    Now the only problem is if the record is stored in a database and not in Kafka, how will our consumer
                    know where to start reading when it is assigned a partition? This is exactly what seek() can be used
                    for. When the consumer starts or when new partitions are assigned, it can look up the offset in
                    the database and seek() to that location.
                     */
                }
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

    public void stop() {
        //When you decide to exit the poll loop, you will need another thread to call consumer.wakeup(). If you are
        //running the consumer loop in the main thread, this can be done from ShutdownHook. Note that consumer.wakeup()
        //is the only consumer method that is safe to call from a different thread. Calling wakeup will cause poll()
        //to exit with WakeupException, or if consumer.wakeup() was called while the thread was not waiting on poll,
        //the exception will be thrown on the next iteration when poll() is called. The WakeupException doesn’t need
        //to be handled, but before exiting the thread, you must call consumer.close(). Closing the consumer will
        //commit offsets if needed and will send the group coordinator a message that the consumer is leaving the group.
        //The consumer coordinator will trigger rebalancing immediately and you won’t need to wait for the session to
        //time out before partitions from the consumer you are closing will be assigned to another consumer in the group.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Starting exit...");
            consumer.wakeup();
//            try {
//                mainThread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }));
    }

    public class SaveOffsetsOnRebalance implements ConsumerRebalanceListener {

        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            //We use an imaginary method here to commit the transaction in the database. The idea here is that the database
            //records and offsets will be inserted to the database as we process the records, and we just need to commit
            //the transactions when we are about to lose the partition to make sure this information is persisted.
            commitDBTransaction();
        }

        private void commitDBTransaction() {
            System.out.println("Committing DB transaction with offset");
        }

        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            //We also have an imaginary method to fetch the offsets from the database, and then we seek() to those records
            //when we get ownership of new partitions
            for (TopicPartition partition : partitions)
                consumer.seek(partition, getOffsetFromDB(partition));
        }
    }

    private long getOffsetFromDB(TopicPartition partition) {
        System.out.println("Fetch last committed offset for partition");
        return 0;
    }

    private void processRecord(ConsumerRecord<String, String> record) {
        System.out.println("Processing record");
    }

    private void storeRecordInDB(ConsumerRecord<String, String> record) {
        System.out.println("Storing record");
    }

    private void updateCurrentOffsets(ConsumerRecord<String, String> record) {
        TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
        OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(record.offset() + 1, "no metadata");
        currentOffsets.put(topicPartition, offsetAndMetadata);
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
