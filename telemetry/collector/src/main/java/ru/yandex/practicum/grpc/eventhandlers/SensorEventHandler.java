package ru.yandex.practicum.grpc.eventhandlers;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getMessageType();

    void handle(SensorEventProto event);
}
