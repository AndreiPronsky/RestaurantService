package org.pronsky.data.entities;

import lombok.Data;
import org.javamoney.moneta.Money;

import java.util.List;

@Data
public class OrderDetails {
    private Long id;
    private OrderStatus orderStatus;
    private List<Product> products;
    private Money totalAmount;

    public enum OrderStatus {
        OPEN,
        CONFIRMED,
        COMPLETED,
        CANCELLED
    }
}
