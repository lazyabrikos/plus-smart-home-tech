package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.enums.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private UUID paymentId;
    @Column(name = "total_payment")
    private BigDecimal totalPayment;
    @Column(name = "delivery_total")
    private BigDecimal deliveryTotal;
    @Column(name = "fee_total")
    private BigDecimal feeTotal;
    @Column(name = "product_total")
    private BigDecimal productTotal;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_state")
    private PaymentState paymentState = PaymentState.PENDING;
    @Column(name = "order_id")
    private UUID orderId;
}