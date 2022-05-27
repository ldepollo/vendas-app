package com.letscode.vendasapp.handler;

import com.letscode.vendasapp.domain.Sale;
import com.letscode.vendasapp.dto.UserRequestDto;
import com.letscode.vendasapp.repository.SalesRepository;
import com.letscode.vendasapp.service.CartService;
import com.letscode.vendasapp.service.SaleService;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SalesHandler {

    private final SaleService saleService;
    private final CartService cartService;
    private final SalesRepository salesRepository;

    public SalesHandler(SaleService saleService, CartService cartService, SalesRepository salesRepository) {
        this.saleService = saleService;
        this.cartService = cartService;
        this.salesRepository = salesRepository;
    }

    public Mono<ServerResponse> makePurchase(ServerRequest request) {
        return request.bodyToMono(UserRequestDto.class)
                .flatMap(cartService::getUserCart)
                .flatMap(saleService::makePurchase)
                .flatMap(salesRepository::save)
                .flatMap(sale -> ServerResponse.ok().bodyValue(sale))
                .switchIfEmpty(ServerResponse
                        .unprocessableEntity()
                        .bodyValue("Invalid username or this user does not have a cart yet"));
    }

    public Mono<ServerResponse> getAllPurchases() {
//        return Flux.from(salesRepository.findAll())
//
//                .flatMap(sales -> ServerResponse.ok().bodyValue(sales));
        return null;
    }
}
