package ru.yandex.practicum.grpc.eventhandlers;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
@RequiredArgsConstructor
public class SwitchSensorEventHandler implements SensorEventHandler {

    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    @Value("${topics.sensors}")
    private String sensorTopic;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        SwitchSensorAvro switchSensorAvro = SwitchSensorAvro.newBuilder()
                .setState(event.getSwitchSensorEvent().getState())
                .build();
        kafkaProducer.send(new ProducerRecord<>(sensorTopic, switchSensorAvro));
    }
}
