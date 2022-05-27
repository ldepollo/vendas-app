package com.letscode.vendasapp.gateway;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.RetrySpec;

import java.time.Duration;

@Component
@AllArgsConstructor
public class UserGateway {

    @Value("${usuarios.base.url}")
    private String baseUrl;

    public Mono<String> getUserByName(String name) {
        return WebClient
                .builder()
                .baseUrl(String.format(baseUrl, name))
                .build()
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class, erro ->
                        erro.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(erro)
                );
    }

}
