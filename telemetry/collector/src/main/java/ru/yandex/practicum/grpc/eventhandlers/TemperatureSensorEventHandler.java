package ru.yandex.practicum.grpc.eventhandlers;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
@RequiredArgsConstructor
public class TemperatureSensorEventHandler implements SensorEventHandler {

    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    @Value("${topics.sensors}")
    private String sensorTopic;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        TemperatureSensorAvro temperatureSensorAvro = TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureSensorEvent().getTemperatureC())
                .setTemperatureF(event.getTemperatureSensorEvent().getTemperatureF())
                .build();
        kafkaProducer.send(new ProducerRecord<>(sensorTopic, temperatureSensorAvro));
    }
}
