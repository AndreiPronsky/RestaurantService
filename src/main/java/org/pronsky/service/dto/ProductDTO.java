package org.pronsky.service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private boolean available;
    private List<ProductCategoryDTO> categories;

    @Override
    public String toString() {
        return super.toString();
    }
}
