package com.softwarelabs.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Profile("!integration")
@Slf4j
public class KafkaConfiguration {

	private final Map<String, Object> producerProps;
	private final Map<String, Object> consumerProps;

	@Autowired
	public KafkaConfiguration(@Value("${kafka.bootstrap.servers}") String bootstrapServers) {
		this.producerProps = producerProps(bootstrapServers);
		this.consumerProps = consumerProps(bootstrapServers);
	}

	private Map<String, Object> producerProps(String bootstrapServers
	) {
		final Map<String, Object> props = new ConcurrentHashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		return props;
	}

	private Map<String, Object> consumerProps(
			String bootstrapServers) {

		final Map<String, Object> props = new ConcurrentHashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);
		return props;
	}

	@Bean
	public KafkaConsumerFactory<String, String> kafkaConsumerFactory() {
		return new KafkaConsumerFactory<>(consumerProps);
	}

	@Bean
	public KafkaProducerFactory<String, String> kafkaProducerFactory() {
		return new KafkaProducerFactory<>(producerProps);
	}
}
