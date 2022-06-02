package com.letscode.vendasapp;

import com.letscode.vendasapp.domain.Cart;
import com.letscode.vendasapp.domain.Product;
import com.letscode.vendasapp.dto.CartRequestDto;
import com.letscode.vendasapp.gateway.ProductGateway;
import com.letscode.vendasapp.gateway.UserGateway;
import com.letscode.vendasapp.repository.CartRepository;
import com.letscode.vendasapp.service.CartService;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ExtendWith(SpringExtension.class)
@Import({CartService.class, ProductGateway.class, UserGateway.class})
class CartServiceTests {

    public static MockWebServer mockWebServer;

    @Autowired
    private CartService cartService;

    @MockBean
    private CartRepository cartRepository;

    static Dispatcher dispatcher = new Dispatcher() {
        @NotNull
        @Override
        public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) throws InterruptedException {
            return switch (recordedRequest.getPath()) {
                case "/product/1" -> new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .setBody("{\n" +
                                "\t\"id\": \"230d9600-1dae-456b-8254-a82088505cc9\",\n" +
                                "\t\"sku\": \"1\",\n" +
                                "\t\"name\": \"Bola\",\n" +
                                "\t\"value\": 10.0\n" +
                                "}");
                case "/product/2" -> new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .setBody("{\n" +
                                "\t\"id\": \"6778bbd9-4a55-4ece-9c9a-0f5fba9af0d5\",\n" +
                                "\t\"sku\": \"2\",\n" +
                                "\t\"name\": \"Brinquedo\",\n" +
                                "\t\"value\": 10.0\n" +
                                "}");
                case "/user/Lucas" -> new MockResponse()
                        .setResponseCode(200)
                        .setBody("Lucas");
                case "/user/Ricardo" -> new MockResponse()
                        .setResponseCode(404)
                        .setBody("Usuario não existe");
                default -> new MockResponse()
                        .setResponseCode(500)
                        .setBody("Requisição não mapeada");
            };
        }
    };

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(9000);
    }

    @Test
    void shouldGetCartsFromUser() {
        Cart userCart = new Cart("Lucas");

        when(cartRepository.findAllByUsername("Lucas")).thenReturn(Flux.just(userCart));

        StepVerifier.create(cartService.getUserCart("Lucas"))
                .expectNextMatches(list -> list.get(0).equals(userCart))
                .verifyComplete();
    }

    @Test
    void shouldGetOpenCartFromUser() {
        Cart userCart = new Cart("Lucas");
        Cart userCartClosed = new Cart("Lucas");
        userCartClosed.setStatus("closed");

        when(cartRepository.findAllByUsername("Lucas")).thenReturn(Flux.just(userCart, userCartClosed));

        StepVerifier.create(cartService.getOpenUserCart("Lucas"))
                .expectNextMatches(cart -> cart.equals(userCart))
                .verifyComplete();
    }

    @Test
    void shouldAddProductToCart() {
        Cart userCart = new Cart("Lucas");

        when(cartRepository.findAllByUsername("Lucas")).thenReturn(Flux.just(userCart));

        Assertions.assertThat(userCart.getProducts()).isEmpty();

        StepVerifier.create(cartService.addProductToCart(new CartRequestDto("Lucas", "1")))
                .expectNextMatches(c -> c.getProducts().size() == 1)
                .verifyComplete();

        StepVerifier.create(cartService.addProductToCart(new CartRequestDto("Lucas", "2")))
                .expectNextMatches(c -> c.getProducts().size() == 2)
                .verifyComplete();
    }

    @Test
    void shouldRemoveItemFromCart() {
        Cart userCart = new Cart("Lucas");
        Product product = new Product();
        product.setId(UUID.fromString("230d9600-1dae-456b-8254-a82088505cc9"));
        product.setSku("1");
        product.setName("Bola");
        product.setValue(10.0);
        userCart.insertProduct(product);

        when(cartRepository.findAllByUsername("Lucas")).thenReturn(Flux.just(userCart));

        Assertions.assertThat(userCart.getProducts()).hasSize(1);

        StepVerifier.create(cartService.removeItemFromCart(new CartRequestDto("Lucas", "1")))
                .expectNextMatches(c -> c.getProducts().size() == 0)
                .verifyComplete();
    }

    @Test
    void shouldCloseUserCart() {
        Cart userCart = new Cart("Lucas");

        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(userCart));

        Assertions.assertThat(userCart.getStatus()).isEqualTo("open");

        StepVerifier.create(cartService.closeUserCart(userCart))
                .expectNextMatches(c -> c.getStatus().equals("closed"))
                .verifyComplete();
    }
}
