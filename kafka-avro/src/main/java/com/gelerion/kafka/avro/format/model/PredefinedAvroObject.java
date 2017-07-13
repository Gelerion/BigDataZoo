package com.gelerion.kafka.avro.format.model;

import com.google.common.collect.ImmutableMap;
import customerManagement.avro.Customer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class PredefinedAvroObject {

	// 1 - kafka-zookeeper
	// 2 - kafka
	// 3 - schema registry
	public static void main(String[] args) {
		ImmutableMap<String, Object> kafkaProps = ImmutableMap.<String, Object>builder()
				.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
				.put("key.serializer", KafkaAvroSerializer.class.getName())
				.put("value.serializer", KafkaAvroSerializer.class.getName())
				.put("schema.registry.url", "localhost:2181").build();

		String topic = "customerContacts";
		int wait = 500;

		KafkaProducer<String, Customer> producer = new KafkaProducer<>(kafkaProps);

		// produce 3 events
		for (int i = 0; i < 3; i++) {
			Customer customer = Customer.newBuilder()
                .setId(i).setEmail("email" + i)
                .setName("Den" + i)
                .build();

			System.out.println(customer);
			ProducerRecord<String, Customer> record =
			    new ProducerRecord<>(topic, String.valueOf(customer.getId()), customer);
			// file and forget
			producer.send(record);
		}
	}
}
