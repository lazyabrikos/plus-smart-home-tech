package ru.yandex.practicum.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AssemblyProductsForOrderRequest {
    @NotNull
    UUID shoppingCartId;
    @NotNull
    UUID orderId;
}