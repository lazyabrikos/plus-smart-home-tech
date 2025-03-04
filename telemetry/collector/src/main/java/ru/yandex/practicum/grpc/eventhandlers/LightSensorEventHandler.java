package ru.yandex.practicum.grpc.eventhandlers;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {

    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    @Value("${topics.sensors}")
    private String sensorTopic;


    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        SensorEventAvro lightSensorAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(),
                        event.getTimestamp().getNanos()))
                .setPayload(LightSensorAvro.newBuilder()
                        .setLinkQuality(event.getLightSensorEvent().getLinkQuality())
                        .setLuminosity(event.getLightSensorEvent().getLuminosity())
                        .build())
                .build();
        kafkaProducer.send(new ProducerRecord<>(sensorTopic, lightSensorAvro));
    }
}
