package com.fly.kafka;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class Consumer {
	public static void main(String[] args) {
		 Properties props = new Properties();
	     props.put("bootstrap.servers", "192.168.88.8:9092");
	 //    props.put("zookeeper.connect", "192.168.88.8:2180,192.168.88.8:2181,192.168.88.8:2182/kafka");
	     props.put("group.id", "group-1");
	     props.put("enable.auto.commit", "true");
	     props.put("auto.commit.interval.ms", "1000");
	     props.put("session.timeout.ms", "30000");
	     props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
	     consumer.subscribe(Arrays.asList("test","my-topic"));
		while (true) {
			/*Collection<TopicPartition> partitions = new ArrayList<TopicPartition>();
			TopicPartition tp = new TopicPartition("test", 0);
			partitions.add(tp);
			consumer.seekToBeginning(partitions);*/
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records){
				System.out.printf("offset = %d, key = %s, value = %s \n",
						record.offset(), record.key(), record.value());
			}
		}
	}
}
