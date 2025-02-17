package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mappers.hub.HubMapper;
import ru.yandex.practicum.mappers.sensors.SensorMapper;
import ru.yandex.practicum.model.hub.*;
import ru.yandex.practicum.model.sensor.*;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    @Value("${topics.sensors}")
    private String sensorTopic;

    @Value("${topics.hubs}")
    private String hubTopic;

    @Override
    public void addSensorEvent(SensorEvent sensorEvent) {
        switch (sensorEvent.getType()) {
            case LIGHT_SENSOR_EVENT -> kafkaProducer
                    .send(new ProducerRecord<>(sensorTopic,
                            SensorMapper.mapToLightSensorAvro((LightSensorEvent) sensorEvent)));
            case MOTION_SENSOR_EVENT -> kafkaProducer.send(
                    new ProducerRecord<>(sensorTopic,
                            SensorMapper.mapToMotionSensorAvro(((MotionSensorEvent) sensorEvent))));
            case CLIMATE_SENSOR_EVENT -> kafkaProducer.send(
                    new ProducerRecord<>(sensorTopic,
                            SensorMapper.mapToClimateSensorAvro(((ClimateSensorEvent) sensorEvent))));
            case SWITCH_SENSOR_EVENT -> kafkaProducer.send(
                    new ProducerRecord<>(sensorTopic,
                            SensorMapper.mapToSwitchSensorAvro(((SwitchSensorEvent) sensorEvent))));
            case TEMPERATURE_SENSOR_EVENT -> kafkaProducer.send(
                    new ProducerRecord<>(sensorTopic,
                            SensorMapper.mapToTemperatureSensorAvro(((TemperatureSensorEvent) sensorEvent))));
        }
    }

    @Override
    public void addHubEvent(HubEvent hubEvent) {
        switch (hubEvent.getType()) {
            case DEVICE_ADDED -> kafkaProducer.send(
                    new ProducerRecord<>(sensorTopic,
                            HubMapper.mapToDeviceAddedEventAvro((DeviceAddedEvent) hubEvent)));
            case DEVICE_REMOVED -> kafkaProducer.send(
                    new ProducerRecord<>(sensorTopic,
                            HubMapper.mapToDeviceRemovedEventAvro((DeviceRemovedEvent) hubEvent)));
            case SCENARIO_ADDED -> kafkaProducer.send(
                    new ProducerRecord<>(sensorTopic,
                            HubMapper.mapToScenarioAddedEventAvro((ScenarioAddedEvent) hubEvent)));
            case SCENARIO_REMOVED -> kafkaProducer.send(
                    new ProducerRecord<>(sensorTopic,
                            HubMapper.mapToScenarioRemovedEventAvro((ScenarioRemovedEvent) hubEvent)));
        }
    }
}
