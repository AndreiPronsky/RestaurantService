package org.pronsky.data.entities;

import lombok.Data;

@Data
public class ProductCategory {
    private Long id;
    private String name;
    private CategoryType type;

    public enum CategoryType {
        VEGETABLE,
        FRUIT,
        MEAT,
        GRAIN
    }
}
