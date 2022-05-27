package com.letscode.vendasapp.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class Product {

    private UUID id;
    private String sku;
    private String name;
    private double value;

}
