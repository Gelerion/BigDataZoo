package com.gelerion.kafka.avro.format

import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.collection.JavaConverters.mapAsJavaMapConverter

object GenericAvroObject {

  def main(args: Array[String]): Unit = {
    val props: Map[String, AnyRef] = Map(
      CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
      "key.serializer" -> classOf[KafkaAvroSerializer].getName,
      "value.serializer" -> classOf[KafkaAvroSerializer].getName,
      "schema.registry.url" -> "http://localhost:8081"
    )

    val genericSchema =
      """
        {"namespace": "customerManagement.avro",
         "type": "record",
         "name": "Customer",
         "fields": [
             {"name": "id", "type": "int"},
             {"name": "name",  "type": "string"},
             {"name": "email", "type": ["null", "string"], "default": "null"}
         ]
        }
      """.stripMargin

    val parser = new Schema.Parser()
    val schema = parser.parse(genericSchema)

    val producer = new KafkaProducer[Int, GenericRecord](props.asJava)

    val topic = "customerContacts"

    Stream.from(0).take(10).foreach { id =>
      val name = "Denis"
      val email = "a@b"

      val customerRecord = new GenericData.Record(schema)
      customerRecord.put("id", id)
      customerRecord.put("name", name)
      customerRecord.put("email", email)

      val data = new ProducerRecord[Int, GenericRecord](topic, id, customerRecord)
      producer.send(data)
    }

    producer.close()

  }

}
