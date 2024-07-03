package org.pronsky.service.mapper;

import org.pronsky.data.entities.OrderDetails;
import org.pronsky.data.entities.Product;
import org.pronsky.service.dto.OrderDetailsDTO;
import org.pronsky.service.dto.ProductDTO;

@org.mapstruct.Mapper
public interface Mapper {
    OrderDetailsDTO toDto(OrderDetails entity);

    OrderDetails toEntity(OrderDetailsDTO dto);

    ProductDTO toDto(Product entity);

    Product toEntity(ProductDTO dto);
}
