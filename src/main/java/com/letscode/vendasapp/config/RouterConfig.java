package com.letscode.vendasapp.config;

import com.letscode.vendasapp.handler.CartHandler;
import com.letscode.vendasapp.handler.SalesHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> route(CartHandler cartHandler, SalesHandler salesHandler) {
        return RouterFunctions
                .route(POST("/cart/add").and(contentType(APPLICATION_JSON)), cartHandler::addProductToCart)
                .andRoute(DELETE("/cart/remove/{user}/{sku}"), cartHandler::removeItemFromCart)
                .andRoute(GET("/cart/get/{user}"), cartHandler::getUserCart)
                .andRoute(GET("/sale/purchase/{user}"), salesHandler::makePurchase)
                .andRoute(GET("/sale/all"), salesHandler::getAllPurchases);
    }
}
