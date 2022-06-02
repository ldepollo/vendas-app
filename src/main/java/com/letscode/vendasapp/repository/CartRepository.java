package com.letscode.vendasapp.repository;

import com.letscode.vendasapp.domain.Cart;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface CartRepository extends ReactiveMongoRepository<Cart, String> {
    Mono<Cart> findByUsername(String username);
    Flux<Cart> findAllByUsername(String username);
}
