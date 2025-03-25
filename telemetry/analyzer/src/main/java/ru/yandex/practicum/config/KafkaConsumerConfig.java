package ru.yandex.practicum.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    @Value("{spring.kafka.consumer.client-id}")
    private String hubClientId;

    @Value("{spring.kafka.consumer.group-id}")
    private String hubGroupId;

    @Value("{spring.kafka.bootstrap-servers}")
    private String boostrapServers;

    @Value("{spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("{spring.kafka.consumer.value-deserializer}")
    private String valueDeserializer;

    @Value("{spring.kafka.consumer.enable-auto-commit}")
    private String autoCommit;

    @Value("{spring.kafka.consumer.snapshots-client-id}")
    private String snapshotClientId;

    @Value("{spring.kafka.consumer.snapshots-group-id}")
    private String snapshotGroupId;

    @Value("{spring.kafka.consumer.snapshots-deserializer}")
    private String snapshotDeserializer;

    @Bean
    public KafkaConsumer<String, HubEventAvro> getHubEventProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, hubClientId);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, hubGroupId);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        return new KafkaConsumer<>(properties);
    }

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> getSnapshotProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, snapshotClientId);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotGroupId);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapshotDeserializer);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);

        return new KafkaConsumer<>(properties);
    }
}
