package org.pronsky.service.mapper;

import org.pronsky.data.entities.OrderApproval;
import org.pronsky.data.entities.OrderDetails;
import org.pronsky.data.entities.Product;
import org.pronsky.service.dto.OrderApprovalDTO;
import org.pronsky.service.dto.OrderDetailsDTO;
import org.pronsky.service.dto.ProductDTO;

@org.mapstruct.Mapper
public interface Mapper {
    OrderApprovalDTO toDto(OrderApproval orderApproval);
    OrderApproval toEntity(OrderApprovalDTO orderApprovalDTO);
    OrderDetailsDTO toDto(OrderDetails orderDetails);
    OrderDetails toEntity(OrderDetailsDTO orderDetailsDTO);
    ProductDTO toDto(Product product);
    ProductDTO toEntity(ProductDTO productDTO);
}
