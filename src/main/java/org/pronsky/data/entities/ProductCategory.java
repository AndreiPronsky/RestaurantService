package org.pronsky.data.entities;

import lombok.Data;

import java.util.Set;

@Data
public class ProductCategory {
    private Long id;
    private String name;
    private Set<CategoryType> types;

    public enum CategoryType {
        PERISHABLE,
        LONG_TERM,
        FRIDGE_STORAGE,
        FREEZER_STORAGE,
        PANTRY_STORAGE
    }
}
