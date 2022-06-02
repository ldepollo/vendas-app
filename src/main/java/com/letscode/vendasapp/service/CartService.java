package com.letscode.vendasapp.service;

import com.letscode.vendasapp.domain.Cart;
import com.letscode.vendasapp.dto.CartRequestDto;
import com.letscode.vendasapp.gateway.ProductGateway;
import com.letscode.vendasapp.gateway.UserGateway;
import com.letscode.vendasapp.repository.CartRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
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
        return getOpenUserCart(cartRequestDto.getUsername())
                .flatMap(cart -> insertItemToCart(cart, cartRequestDto.getSku()));
    }

    public Mono<Cart> insertItemToCart(Cart cart, String sku) {
        return Mono.just(sku)
                .flatMap(s -> productGateway.getProductBySku(sku))
                .map(cart::insertProduct)
                .map(s -> cart);
    }

    public Mono<Cart> removeItemFromCart(CartRequestDto cartRequestDto) {
        return getOpenUserCart(cartRequestDto.getUsername())
                .map(cart -> {
                    cart.setProducts(cart.getProducts()
                            .stream()
                            .filter(p -> !p.getSku().equals(cartRequestDto.getSku()))
                            .collect(Collectors.toList()));
                    return cart;
                });
    }

    public Mono<Cart> closeUserCart(Cart cart) {
        return Mono.just(cart)
                .flatMap(c -> {
                    cart.setStatus("closed");
                    return cartRepository.save(cart);
                });
    }

    public Mono<List<Cart>> getUserCart(String username) {
        Flux<Cart> cartFlux = Flux.just(username)
                .flatMap(userGateway::getUserByName)
                .flatMap(cartRepository::findAllByUsername);

        return cartFlux.collectList();
    }

    public Mono<Cart> getOpenUserCart(String username) {
        return getUserCart(username)
                .map(list -> list.stream()
                        .filter(cart -> cart.getStatus().equals("open"))
                        .collect(Collectors.toList())
                        .stream().findFirst())
                .map(optional -> optional.orElse(new Cart(username)));
    }
}
