package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.enums.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID orderId;
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderState orderState = OrderState.NEW;
    @ElementCollection
    @CollectionTable(name = "order_products", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Integer> products;
    @Column(name = "shopping_cart_id")
    private UUID shoppingCartId;
    @Column(name = "payment_id")
    private UUID paymentId;
    @Column(name = "delivery_id")
    private UUID deliveryId;
    @Column(name = "delivery_weight")
    private Double deliveryWeight;
    @Column(name = "delivery_volume")
    private Double deliveryVolume;
    private Boolean fragile;
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @Column(name = "delivery_price")
    private BigDecimal deliveryPrice;
    @Column(name = "product_price")
    private BigDecimal productPrice;
}
