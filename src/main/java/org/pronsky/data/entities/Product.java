package org.pronsky.data.entities;

import lombok.Data;
import org.javamoney.moneta.Money;

@Data
public class Product {
    private Long id;
    private String name;
    private Money price;
    private Integer quantity;
    private boolean available;
}
