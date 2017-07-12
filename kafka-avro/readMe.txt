# Start Zookeeper
$ bin/zookeeper-server-start config/zookeeper.properties

# Start Kafka
$ bin/kafka-server-start config/server.properties

# https://github.com/confluentinc/schema-registry - provides a serving layer for your metadata
# Start Schema Registry
$ bin/schema-registry-start config/schema-registry.properties

# Create topic
$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 \
  --partitions 1 --topic CustomerCountry