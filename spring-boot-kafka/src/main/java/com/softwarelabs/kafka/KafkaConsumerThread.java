package com.softwarelabs.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static com.softwarelabs.kafka.KafkaConfigurationConstant.POLLING_TIME;

@Slf4j
public class KafkaConsumerThread<T, K, V> {

	private Consumer<K, V> consumer;
	private ObjectMapper mapper;
	private EventConsumer<T> eventConsumer;
	private OffsetCommitCallback errorLoggingCommitCallback() {
		return new ErrorLoggingCommitCallback();
	}

	public KafkaConsumerThread(EventConsumer<T> eventConsumer, Consumer<K, V> consumer, ObjectMapper mapper) {
		log.info("Starting Kafka consumer");
		this.consumer = consumer;
		this.eventConsumer = eventConsumer;
		this.consumer.subscribe(Collections.singletonList(eventConsumer.topicName()));
		this.mapper = mapper;
	}

	public void start(){
		Thread consumer = new Thread(() -> {
			run();
		});
		/*
		 * Starting the thread.
		 */
		consumer.start();
	}

	public void stop(){
		consumer.wakeup();
	}

	private void run() {
		while (true) {
			ConsumerRecords<K, V> consumerRecords = consumer.poll(Duration.ofMillis(POLLING_TIME));
			//print each record.
			consumerRecords.forEach(record -> {
				log.info("Record Key " + record.key());
				log.info("Record value " + record.value());
				log.info("Record partition " + record.partition());
				log.info("Record offset " + record.offset());
				// commits the offset of record to broker.
				T value = null;
				try {
					value = (T) mapper.readValue((String) record.value(), eventConsumer.eventType());
				} catch (IOException e) {
					e.printStackTrace();
				}
				eventConsumer.consume(value);
			});
			consumer.commitAsync(errorLoggingCommitCallback());
		}
	}

	private class ErrorLoggingCommitCallback implements OffsetCommitCallback {

		@Override
		public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
			if (exception != null) {
				log.error("Exception while commiting offsets to Kafka", exception);
			}
		}
	}
}
