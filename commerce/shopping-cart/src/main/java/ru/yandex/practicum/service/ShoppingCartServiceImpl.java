package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.clients.WarehouseClient;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.DeactivateCartException;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;
import ru.yandex.practicum.requests.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository repository;
    private final ShoppingCartMapper mapper;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        checkUsername(username);
        return mapper.toCartDto(repository.findByUsername(username));
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products) {
        checkUsername(username);
        ShoppingCart shoppingCart = repository.findByUsername(username);
        checkCartIsActive(shoppingCart);
        Map<UUID, Integer> oldProducts = shoppingCart.getProducts();
        oldProducts.putAll(products);
        shoppingCart.setProducts(oldProducts);
        BookedProductsDto bookedProductsDto = warehouseClient.checkProductQuantityEnoughForShoppingCart(
                mapper.toCartDto(shoppingCart)
        );
        return mapper.toCartDto(repository.save(shoppingCart));

    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {
        checkUsername(username);
        ShoppingCart shoppingCart = repository.findByUsername(username);
        shoppingCart.setActive(false);
        repository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> products) {
        checkUsername(username);
        ShoppingCart shoppingCart = repository.findByUsername(username);
        checkCartIsActive(shoppingCart);
        Map<UUID, Integer> oldProducts = shoppingCart.getProducts();
        for (UUID id : products) {
            if (oldProducts.containsKey(id)) {
                oldProducts.remove(id);
            } else {
                throw new NoProductsInShoppingCartException("No product in car with id " + id.toString());
            }
        }
        shoppingCart.setProducts(oldProducts);
        return mapper.toCartDto(repository.save(shoppingCart));
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest requestDto) {
        checkUsername(username);
        ShoppingCart shoppingCart = repository.findByUsername(username);
        checkCartIsActive(shoppingCart);
        Map<UUID, Integer> oldProducts = shoppingCart.getProducts();
        if (oldProducts.containsKey(requestDto.getProductId())) {
            oldProducts.put(requestDto.getProductId(), requestDto.getNewQuantity());
        } else {
            throw new NoProductsInShoppingCartException("No product in cart with id = " + requestDto.getProductId());
        }

        BookedProductsDto bookedProductsDto = warehouseClient.checkProductQuantityEnoughForShoppingCart(
                mapper.toCartDto(shoppingCart)
        );

        return mapper.toCartDto(repository.save(shoppingCart));

    }

    private void checkUsername(String username) {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException("Username cannot be empty");
        }
    }

    private void checkCartIsActive(ShoppingCart cart) {
        if (!cart.getActive()) {
            throw new DeactivateCartException("User cart " + cart.getUsername() + " is deactivated");
        }
    }
}
