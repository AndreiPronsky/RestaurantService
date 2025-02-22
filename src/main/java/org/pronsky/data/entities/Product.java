package org.pronsky.data.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private boolean available;
    private Set<ProductCategory> productCategories;
}
