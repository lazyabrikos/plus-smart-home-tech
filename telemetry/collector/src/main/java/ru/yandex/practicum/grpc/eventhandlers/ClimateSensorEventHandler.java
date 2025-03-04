package ru.yandex.practicum.grpc.eventhandlers;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.mappers.sensors.SensorMapper;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;

@Component
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    @Value("${topics.sensors}")
    private String sensorTopic;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        ClimateSensorAvro climateSensorAvro = ClimateSensorAvro.newBuilder()
                .setHumidity(event.getClimateSensorEvent().getHumidity())
                .setCo2Level(event.getClimateSensorEvent().getCo2Level())
                .setTemperatureC(event.getClimateSensorEvent().getTemperatureC())
                .build();
        kafkaProducer.send(new ProducerRecord<>(sensorTopic, climateSensorAvro));
    }
}
