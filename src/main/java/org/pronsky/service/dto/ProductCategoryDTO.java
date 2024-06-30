package org.pronsky.service.dto;

import lombok.Data;
import org.pronsky.data.entities.ProductCategory;

@Data
public class ProductCategoryDTO {
    private Long id;
    private String name;
    private ProductCategory.CategoryType type;

    public enum CategoryType {
        VEGETABLE,
        FRUIT,
        MEAT,
        GRAIN
    }
}
