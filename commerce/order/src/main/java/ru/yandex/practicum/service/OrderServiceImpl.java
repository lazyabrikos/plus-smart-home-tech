package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.clients.DeliveryClient;
import ru.yandex.practicum.clients.PaymentClient;
import ru.yandex.practicum.clients.ShoppingCartClient;
import ru.yandex.practicum.clients.WarehouseClient;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.enums.OrderState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.requests.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.requests.CreateNewOrderRequest;
import ru.yandex.practicum.requests.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class OrderServiceImpl implements OrderService {

    private final ShoppingCartClient shoppingCartClient;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException("Username cannot be empty");
        }

        ShoppingCartDto shoppingCartDto = shoppingCartClient.getShoppingCart(username);
        List<Order> orders = orderRepository.findByShoppingCartId(shoppingCartDto.getShoppingCartId());
        return orderMapper.toOrdersDto(orders);
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        Order order = Order.builder()
                .shoppingCartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .orderState(OrderState.NEW)
                .build();
        Order newOrder = orderRepository.save(order);

        BookedProductsDto bookedProducts = warehouseClient.assemblyProductsForOrder(
                new AssemblyProductsForOrderRequest(
                        request.getShoppingCart().getShoppingCartId(),
                        newOrder.getOrderId()
                ));

        newOrder.setFragile(bookedProducts.getFragile());
        newOrder.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        newOrder.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        newOrder.setProductPrice(paymentClient.productCost(orderMapper.toOrderDto(newOrder)));

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(newOrder.getOrderId())
                .fromAddress(warehouseClient.getWarehouseAddress())
                .toAddress(request.getDeliveryAddress())
                .build();
        newOrder.setDeliveryId(deliveryClient.planDelivery(deliveryDto).getDeliveryId());

        paymentClient.payment(orderMapper.toOrderDto(newOrder));
        return orderMapper.toOrderDto(newOrder);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Order not found"));
        warehouseClient.acceptReturn(request.getProducts());
        order.setOrderState(OrderState.PRODUCT_RETURNED);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->  new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.PAID);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->  new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.PAYMENT_FAILED);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->  new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.DELIVERED);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->  new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.DELIVERY_FAILED);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto complete(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->  new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.COMPLETED);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found"));
        order.setTotalPrice(paymentClient.getTotalCost(orderMapper.toOrderDto(order)));
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found"));
        order.setDeliveryPrice(deliveryClient.deliveryCost(orderMapper.toOrderDto(order)));
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->  new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.ASSEMBLED);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->  new NoOrderFoundException("Order not found"));
        order.setOrderState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.toOrderDto(order);
    }
}
