package org.pronsky.service.dto;

import lombok.Data;
import org.javamoney.moneta.Money;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private Money price;
    private Integer quantity;
    private boolean available;
}
