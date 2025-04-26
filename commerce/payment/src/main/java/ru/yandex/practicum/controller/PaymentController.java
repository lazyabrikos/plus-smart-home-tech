package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.clients.PaymentClient;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentClient {

    private final PaymentService paymentService;

    @Override
    public Double productCost(OrderDto orderDto) {
        log.info("Calculate product cost");
        return paymentService.productCost(orderDto);
    }

    @Override
    public Double getTotalCost(OrderDto orderDto) {
        log.info("Calculate total cost");
        return paymentService.getTotalCost(orderDto);
    }

    @Override
    public PaymentDto payment(OrderDto orderDto) {
        log.info("Create payment");
        return paymentService.payment(orderDto);
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        log.info("Set payment success");
        paymentService.paymentSuccess(paymentId);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        log.info("Payment failed");
        paymentService.paymentFailed(paymentId);
    }
}
