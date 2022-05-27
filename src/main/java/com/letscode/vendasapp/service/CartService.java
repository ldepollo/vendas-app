package com.letscode.vendasapp.service;

import com.letscode.vendasapp.domain.Cart;
import com.letscode.vendasapp.dto.CartRequestDto;
import com.letscode.vendasapp.dto.UserRequestDto;
import com.letscode.vendasapp.gateway.ProductGateway;
import com.letscode.vendasapp.gateway.UserGateway;
import com.letscode.vendasapp.repository.CartRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class CartService {

    private final ProductGateway productGateway;
    private final UserGateway userGateway;
    private final CartRepository cartRepository;

    public CartService(ProductGateway productGateway, UserGateway userGateway, CartRepository cartRepository) {
        this.productGateway = productGateway;
        this.userGateway = userGateway;
        this.cartRepository = cartRepository;
    }

    public Mono<Cart> addProductToCart(CartRequestDto cartRequestDto) {
        return Mono.just(cartRequestDto)
                .flatMap(user -> userGateway.getUserByName(cartRequestDto.getUsername()))
                .flatMap(username -> cartRepository.findByUsername(username)
                        .switchIfEmpty(Mono.just(new Cart(username))))
                .map(cart -> {
                    productGateway.getProductBySku(cartRequestDto.getSku())
                            .map(product -> cart.getProducts().add(product));
                    return cart;
                });
    }

    public Mono<Cart> removeItemFromCart(CartRequestDto cartRequestDto) {
        return Mono.just(cartRequestDto)
                .flatMap(user -> userGateway.getUserByName(cartRequestDto.getUsername()))
                .flatMap(cartRepository::findByUsername)
                .map(cart -> {
                    cart.setProducts(cart.getProducts()
                            .stream()
                            .filter(p -> !p.getSku().equals(cartRequestDto.getSku()))
                            .collect(Collectors.toList()));
                    return cart;
                });
    }

    public Mono<Cart> getUserCart(UserRequestDto userRequestDto) {
        return Mono.just(userRequestDto)
                .flatMap(user -> userGateway.getUserByName(userRequestDto.getUsername()))
                .flatMap(cartRepository::findByUsername);
    }
}
