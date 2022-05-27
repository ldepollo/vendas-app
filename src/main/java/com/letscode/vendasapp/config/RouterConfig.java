package com.letscode.vendasapp.config;

import com.letscode.vendasapp.handler.CartHandler;
import com.letscode.vendasapp.handler.SalesHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> route(SalesHandler salesHandler, CartHandler cartHandler) {
        return RouterFunctions
                .route(POST("/cart/add/{user}/{sku}").and(contentType(APPLICATION_JSON)), cartHandler::addProductToCart)
                .andRoute(DELETE("/cart/remove/{user}/{sku}").and(contentType(APPLICATION_JSON)), cartHandler::removeItemFromCart)
                .andRoute(GET("/cart/{user}").and(contentType(APPLICATION_JSON)), cartHandler::getUserCart)
                .andRoute(GET("/sale/{user}").and(contentType(APPLICATION_JSON)), salesHandler::makePurchase)
                .andRoute(GET("/sale/all").and(contentType(APPLICATION_JSON)), salesHandler::getAllPurchases);
    }
}
