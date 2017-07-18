           HOW DOES THE PROCESS OF ASSIGNING PARTITIONS TO BROKERS WORK?
When a consumer wants to join a group, it sends a JoinGroup request to the group coordinator.
The first consumer to join the group becomes the group leader. The leader receives a list of
all consumers in the group from the group coordinator (this will include all consumers that
sent a heartbeat recently and are therefore considered alive) and it is responsible for assigning
a subset of partitions to each consumer. It uses an implementation of PartitionAssignor interface
to decide which partitions should be handled by which consumer. Kafka has two built-in partition
assignment policies, which we will discuss in more depth in the configuration section. After
deciding on the partition assignment, the consumer leader sends the list of assignments to the
GroupCoordinator which sends this information to all the consumers. Each consumer only sees his
own assignment - the leader is the only client process that has the full list of consumers in the g
roup and their assignments. This process repeats every time a rebalance happens.


Configuring Consumers:
 FETCH.MIN.BYTES
specify the minimum amount of data that it wants to receive from the broker when fetching records
will want to set this parameter higher than the default if the Consumer is using too much CPU when
there isn’t much data available, or to reduce load on the brokers when you have large number of consumers
 FETCH.MAX.WAIT.MS
control how long to wait. By default Kafka will wait up to 500ms.
 MAX.PARTITION.FETCH.BYTES
property controls the maximum number of bytes the server will return per partition. The default is 1MB
if a topic has 20 partitions, and you have 5 consumers, each consumer will need to have 4MB of memory
available for ConsumerRecords.
must be larger than the largest message a broker will accept (max.message.size property in the broker configuration)
 SESSION.TIMEOUT.MS
amount of time a consumer can be out of contact with the brokers while still considered alive, defaults to 3 seconds.
heartbeat.interval.ms must be lower than session.timeout.ms, and is usually set to a 1/3 of the timeout value
 AUTO.OFFSET.RESET
property controls the behavior of the consumer when it starts reading a partition for which it doesn’t have a committed
offset or if the committed offset it has is invalid (usually because the consumer was down for so long that the record
with that offset was already aged out of the broker). The default is “latest”, which means that lacking a valid offset
the consumer will start reading from the newest records (records which were written after the consumer started running).
The alternative is “earliest”, which means that lacking a valid offset the consumer will read all the data in the partition,
starting from the very beginning
 ENABLE.AUTO.COMMIT
parameter controls whether the consumer will commit offsets automatically and defaults to true. Set it to false if you
prefer to control when offsets are committed, which is necessary to minimize duplicates and avoid missing data. If you
set enable.auto.commit to true then you may also want to control how frequently offsets will be committed using
auto.commit.interval.ms
 PARTITION.ASSIGNMENT.STRATEGY
allows you to choose a partition assignment strategy. The default is
org.apache.kafka.clients.consumer.RangeAssignor which implements the Range strategy.
You can replace it with org.apache.kafka.clients.consumer.RoundRobinAssignor.
 CLIENT.ID
can be any string, and will be used by the brokers to identify messages sent from the client. It is used in logging,
metrics and for quotas
 MAX.POLL.RECORDS
controls the maximum number of records that a single call to poll() will return
 RECEIVE.BUFFER.BYTES AND SEND.BUFFER.BYTES
these are the sizes of the TCP send and receive buffers used by the sockets when writing and reading data.
If these are set to -1, the operating system defaults will be used. It can be a good idea to increase those
when producers or consumers communicate with brokers in a different data center - since those network links
typically have higher latency and lower bandwidth.