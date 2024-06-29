package org.pronsky.service.dto;

import lombok.Data;
import org.javamoney.moneta.Money;
import org.pronsky.data.entities.OrderDetails;
import org.pronsky.data.entities.Product;

import java.util.List;

@Data
public class OrderDetailsDTO {
    private Long id;
    private OrderDetails.OrderStatus orderStatus;
    private List<Product> products;
    private Money totalAmount;

    public enum OrderStatus {
        OPEN,
        CONFIRMED,
        COMPLETED,
        CANCELLED
    }
}
