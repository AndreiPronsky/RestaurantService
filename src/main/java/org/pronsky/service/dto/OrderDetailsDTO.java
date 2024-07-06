package org.pronsky.service.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pronsky.data.entities.OrderDetails;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class OrderDetailsDTO {
    private Long id;
    private OrderDetails.OrderStatus orderStatus;
    private List<ProductDTO> products;
    private BigDecimal totalAmount;

    public enum OrderStatus {
        OPEN,
        CONFIRMED,
        COMPLETED,
        CANCELLED
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
