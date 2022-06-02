package com.letscode.vendasapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartRequestDto {

    private String username;
    private String sku;

}
