package com.letscode.vendasapp;

import com.letscode.vendasapp.domain.Cart;
import com.letscode.vendasapp.domain.Product;
import com.letscode.vendasapp.service.SaleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.UUID;

@WebFluxTest
@ExtendWith(SpringExtension.class)
@Import({SaleService.class})
class SaleServiceTests {

    @Autowired
    private SaleService saleService;

    @Test
    void shouldMakeANewSaleObject() {
        Cart userCart = new Cart("Lucas");
        userCart.setId("1");
        Product product = new Product();
        product.setId(UUID.fromString("230d9600-1dae-456b-8254-a82088505cc9"));
        product.setSku("1");
        product.setName("Bola");
        product.setValue(10.0);
        userCart.insertProduct(product);

        StepVerifier.create(saleService.makePurchase(userCart))
                .expectNextMatches(sale -> sale.getCartId().equals("1") &&
                            sale.getUsername().equals("Lucas") &&
                            sale.getTotalAmount().equals(10.0))
                .verifyComplete();
    }
}
