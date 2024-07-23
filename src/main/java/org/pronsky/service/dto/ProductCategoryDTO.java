package org.pronsky.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;

@Data
public class ProductCategoryDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("types")
    private Set<CategoryType> types;

    public enum CategoryType {
        PERISHABLE,
        LONG_TERM,
        FRIDGE_STORAGE,
        FREEZER_STORAGE,
        PANTRY_STORAGE
    }
}
