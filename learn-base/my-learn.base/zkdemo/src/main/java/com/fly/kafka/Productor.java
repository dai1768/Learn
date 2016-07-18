package com.fly.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Productor {
	
	public static void main(String[] args) {
		 Properties props = new Properties();
		 props.put("bootstrap.servers", "192.168.88.8:9092");
		 props.put("acks", "all");
		 props.put("retries", 0);
		 props.put("batch.size", 16384);
		 props.put("linger.ms", 1);
		 props.put("buffer.memory", 33554432);
		 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		 Producer<String, String> producer = new KafkaProducer<>(props);
		 for(int i = 0; i < 10000; i++){
			ProducerRecord<String, String> record = new ProducerRecord("test",0,"1",i+"");
			// producer.send(new ProducerRecord<String, String>("test", Integer.toString(i), Integer.toString(i)));
			producer.send(record );
		 }
		 producer.close();
	}

}
