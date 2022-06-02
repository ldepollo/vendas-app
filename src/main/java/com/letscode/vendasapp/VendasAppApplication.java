package com.letscode.vendasapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableReactiveFeignClients
public class VendasAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(VendasAppApplication.class, args);
	}

}
