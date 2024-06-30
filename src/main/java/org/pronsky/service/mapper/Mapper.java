package org.pronsky.service.mapper;

import org.pronsky.data.entities.OrderDetails;
import org.pronsky.data.entities.Product;
import org.pronsky.data.entities.ProductCategory;
import org.pronsky.service.dto.OrderDetailsDTO;
import org.pronsky.service.dto.ProductCategoryDTO;
import org.pronsky.service.dto.ProductDTO;

@org.mapstruct.Mapper
public interface Mapper {
    OrderDetailsDTO toDto(OrderDetails entity);
    OrderDetails toEntity(OrderDetailsDTO dto);
    ProductCategoryDTO toDto(ProductCategory entity);
    ProductCategory toEntity(ProductCategoryDTO dto);
    ProductDTO toDto(Product entity);
    ProductDTO toEntity(ProductDTO dto);
}
