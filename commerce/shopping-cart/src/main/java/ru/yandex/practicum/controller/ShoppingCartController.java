package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.clients.ShoppingCartClient;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.requests.ChangeProductQuantityRequest;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartClient {
    private final ShoppingCartService shoppingCartService;

    @Override
    public ShoppingCartDto getShoppingCart(@RequestParam String username) {
        log.info("Get actual cart for authorized user {}", username);
        return shoppingCartService.getShoppingCart(username);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products) {
        log.info("Add item to cart for user {}", username);
        return shoppingCartService.addProductToShoppingCart(username, products);
    }

    @Override
    public void deactivateCurrentShoppingCart(@RequestParam String username) {
        log.info("Deactivate cart for user {}", username);
        shoppingCartService.deactivateCurrentShoppingCart(username);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(@RequestParam String username,
                                                  @RequestBody List<UUID> products) {
        log.info("Change cart for user {}", username);
        return shoppingCartService.removeFromShoppingCart(username, products);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                                 @RequestBody ChangeProductQuantityRequest requestDto) {
        log.info("Change item quantity in cart for user {}", username);
        return shoppingCartService.changeProductQuantity(username, requestDto);
    }
}