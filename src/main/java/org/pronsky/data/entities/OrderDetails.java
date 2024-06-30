package org.pronsky.data.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDetails {
    private Long id;
    private OrderStatus orderStatus;
    private List<Product> products;
    private BigDecimal totalAmount;

    public enum OrderStatus {
        OPEN,
        CONFIRMED,
        COMPLETED,
        CANCELLED
    }
}
