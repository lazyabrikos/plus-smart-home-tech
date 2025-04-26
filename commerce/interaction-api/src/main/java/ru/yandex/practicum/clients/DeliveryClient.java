package ru.yandex.practicum.clients;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient {

    @PutMapping
    public DeliveryDto planDelivery(@RequestBody @Valid DeliveryDto deliveryDto);

    @PostMapping("/successful")
    public void deliverySuccessful(@RequestBody UUID deliveryId);

    @PostMapping("/picked")
    public void deliveryPicked(@RequestBody UUID deliveryId);

    @PostMapping("/failed")
    public void deliveryFailed(@RequestBody UUID deliveryId);

    @PostMapping("/cost")
    public Double deliveryCost(@RequestBody @Valid OrderDto orderDto);
}
