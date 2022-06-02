package com.letscode.vendasapp.handler;

import com.letscode.vendasapp.dto.CartRequestDto;
import com.letscode.vendasapp.repository.CartRepository;
import com.letscode.vendasapp.service.CartService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class CartHandler {

    private final CartService cartService;
    private final CartRepository cartRepository;

    public CartHandler(CartService cartService, CartRepository cartRepository) {
        this.cartService = cartService;
        this.cartRepository = cartRepository;
    }

    public Mono<ServerResponse> addProductToCart(ServerRequest request) {
        return request.bodyToMono(CartRequestDto.class)
                .flatMap(cartService::addProductToCart)
                .flatMap(cartRepository::save)
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart))
                .switchIfEmpty(ServerResponse.unprocessableEntity()
                        .bodyValue("Invalid username or product sku. Enter either existing ones."));
    }

    public Mono<ServerResponse> removeItemFromCart(ServerRequest request) {
        return Mono.just(new CartRequestDto(request.pathVariable("user"), request.pathVariable("sku")))
                .flatMap(cartService::removeItemFromCart)
                .flatMap(cartRepository::save)
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart))
                .switchIfEmpty(ServerResponse.unprocessableEntity()
                        .bodyValue("Invalid username. Enter an existing one."));
    }

    public Mono<ServerResponse> getUserCart(ServerRequest request) {
        return Mono.just(request.pathVariable("user"))
                .flatMap(cartService::getUserCart)
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart))
                .switchIfEmpty(ServerResponse.unprocessableEntity()
                        .bodyValue("Invalid username or this user does not have a cart yet"));
    }

}
