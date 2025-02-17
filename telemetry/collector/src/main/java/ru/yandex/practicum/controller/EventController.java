package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.EventService;

@Slf4j
@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/sensors")
    public SensorEvent collectSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        log.info("Got POST request /events/sensor with body = {}", sensorEvent);
        SensorEvent response =  eventService.addSensorEvent(sensorEvent);
        log.info("Send response with body = {}", response);
        return response;
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        eventService.addHubEvent(hubEvent);
    }
}
