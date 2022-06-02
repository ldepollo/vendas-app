package com.letscode.vendasapp.gateway;

import com.letscode.vendasapp.domain.Product;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductGateway {

    @Value("${produtos.base.url}")
    private String baseUrl;

    public Mono<Product> getProductBySku(String sku) {
        return WebClient
                .builder()
                .baseUrl(String.format(baseUrl, sku))
                .build()
                .get()
                .retrieve()
                .bodyToMono(Product.class)
                .onErrorResume(WebClientResponseException.class, erro ->
                            erro.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(erro)
                );
    }

}
