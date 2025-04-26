package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.clients.OrderClient;
import ru.yandex.practicum.clients.ShoppingStoreClient;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.PaymentState;
import ru.yandex.practicum.exception.NoPaymentFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Override
    public PaymentDto payment(OrderDto orderDto) {
        Double deliveryPrice = orderDto.getDeliveryPrice();
        Double productPrice = orderDto.getProductPrice();
        Double totalPrice = orderDto.getTotalPrice();
        if (deliveryPrice == null || productPrice == null || totalPrice == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Not enough information to create payment");
        }
        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .totalPayment(orderDto.getTotalPrice())
                .productTotal(orderDto.getProductPrice())
                .deliveryTotal(orderDto.getDeliveryPrice())
                .feeTotal(orderDto.getTotalPrice() * 0.1)
                .paymentState(PaymentState.PENDING)
                .build();
        return paymentMapper.toPaymentDto(paymentRepository.save(payment));
    }

    @Override
    public Double productCost(OrderDto orderDto) {
        double productCost = 0.0;
        Map<UUID, Long> products = orderDto.getProducts();
        if (products == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Not enough info to calculate productCost");
        }
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            ProductDto product = shoppingStoreClient.getProduct(entry.getKey());
            productCost += product.getPrice().doubleValue() * entry.getValue();
        }
        return productCost;
    }

    @Override
    public Double getTotalCost(OrderDto orderDto) {
        if (orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Not enough info");
        }
        return orderDto.getProductPrice() + orderDto.getProductPrice() * 0.1 + orderDto.getDeliveryPrice();
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new NoPaymentFoundException("No payment with id = " + paymentId));
        payment.setPaymentState(PaymentState.SUCCESS);
        orderClient.payment(payment.getOrderId());
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new NoPaymentFoundException("No payment with id = " + paymentId));
        payment.setPaymentState(PaymentState.FAILED);
        orderClient.paymentFailed(payment.getOrderId());

    }
}
