package com.letscode.vendasapp.service;

import com.letscode.vendasapp.domain.Cart;
import com.letscode.vendasapp.domain.Sale;
import com.letscode.vendasapp.repository.SalesRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class SaleService {

    private final SalesRepository salesRepository;

    public SaleService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    public Mono<Sale> makePurchase(Cart cart) {
        return Mono.just(cart)
                .map(c -> new Sale(c.getId(), c.getUsername(), getTotalAmount(c)));
    }

    private Double getTotalAmount(Cart cart) {
        AtomicReference<Double> totalAmount = new AtomicReference<>(0.0);
        cart.getProducts().forEach(product -> totalAmount.updateAndGet(v -> v + product.getValue()));
        return totalAmount.get();
    }
}
