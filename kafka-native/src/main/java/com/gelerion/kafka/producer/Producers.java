package com.gelerion.kafka.producer;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;

/**
 * Created by denis.shuvalov on 15/05/2018.
 */
public class Producers {

    public static ImmutableMap<String, Object> localBroker() {
        return ImmutableMap.<String, Object>builder()
                //This list doesn’t need to include all brokers, since the producer will get more information after the initial
                //connection. But it is recommended to include at least two, so in case one broker goes down, the producer
                //will still be able to connect to the cluster.
                .put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") //List of host:port pairs of Kafka brokers
                .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
                .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
                .build();

    }

}
