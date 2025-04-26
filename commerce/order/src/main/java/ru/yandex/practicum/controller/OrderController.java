package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.clients.OrderClient;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.requests.CreateNewOrderRequest;
import ru.yandex.practicum.requests.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;


import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController implements OrderClient {
    private final OrderService orderService;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        log.info("Get user orders: username={}", username);
        List<OrderDto> response = orderService.getClientOrders(username);
        return response;
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("Make new order: request={}", request);
        OrderDto response = orderService.createNewOrder(request);
        return response;
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Calculate delivery cost: orderId={}", orderId);
        OrderDto response = orderService.calculateDeliveryCost(orderId);
        return response;
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Calculate total cost: orderId={}", orderId);
        OrderDto response = orderService.calculateTotalCost(orderId);
        return response;
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("Pay order: orderId={}", orderId);
        OrderDto response = orderService.payment(orderId);
        return response;
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Failed to pay order: orderId={}", orderId);
        OrderDto response = orderService.paymentFailed(orderId);
        return response;
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("Assembly order: orderId={}", orderId);
        OrderDto response = orderService.assembly(orderId);
        return response;
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Failed assembly order: orderId={}", orderId);
        OrderDto response = orderService.assemblyFailed(orderId);
        return response;
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("Delivery order: orderId={}", orderId);
        OrderDto response = orderService.delivery(orderId);
        return response;
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Failed to deliver order: orderId={}", orderId);
        OrderDto response = orderService.deliveryFailed(orderId);
        return response;
    }

    @Override
    public OrderDto complete(UUID orderId) {
        log.info("Complete order: orderId={}", orderId);
        OrderDto response = orderService.complete(orderId);
        return response;
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("Return order: request={}", request);
        OrderDto response = orderService.productReturn(request);
        return response;
    }
}