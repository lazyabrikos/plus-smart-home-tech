package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.clients.OrderClient;
import ru.yandex.practicum.clients.WarehouseClient;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.requests.ShippedToDeliveryRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    private static final double BASERATE = 5.0;
    private static final String ADDRESS1 = "ADDRESS_1";
    private static final String ADDRESS2 = "ADDRESS_2";

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryRepository.findById(deliveryDto.getDeliveryId())
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery not found"));
        delivery.setDeliveryState(DeliveryState.CREATED);
        return deliveryMapper.toDeliveryDto(deliveryRepository.save(delivery));
    }

    @Override
    public void deliverySuccessful(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery not found"));
        delivery.setDeliveryState(DeliveryState.CREATED);
        orderClient.complete(delivery.getOrderId());
    }

    @Override
    public void deliveryPicked(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new NoDeliveryFoundException("Delivery not found"));
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        orderClient.assembly(delivery.getOrderId());
        ShippedToDeliveryRequest deliveryRequest = new ShippedToDeliveryRequest(
                delivery.getOrderId(), delivery.getDeliveryId());
        warehouseClient.shippedToDelivery(deliveryRequest);
    }

    @Override
    public void deliveryFailed(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new NoDeliveryFoundException("Delivery not found"));
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderClient.assemblyFailed(delivery.getOrderId());
    }

    @Override
    public Double deliveryCost(OrderDto orderDto) {
        Delivery delivery = deliveryRepository.findByOrderId(orderDto.getOrderId()).orElseThrow(
                () -> new NoDeliveryFoundException("Delivery not found"));

        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();

        double addressCost = switch (warehouseAddress.getCity()) {
            case ADDRESS1 -> BASERATE * 1;
            case ADDRESS2 -> BASERATE * 2;
            default -> throw new IllegalStateException(String.format("Unexpected value: %s",
                    warehouseAddress.getCity()));
        };
        double deliveryCost = BASERATE + addressCost;
        if (orderDto.getFragile()) deliveryCost += deliveryCost * 0.2;
        deliveryCost += orderDto.getDeliveryWeight() * 0.3;
        deliveryCost += orderDto.getDeliveryVolume() * 0.2;
        if (!warehouseAddress.getStreet().equals(delivery.getToAddress().getStreet())) {
            deliveryCost += deliveryCost * 0.2;
        }
        return deliveryCost;
    }
}
