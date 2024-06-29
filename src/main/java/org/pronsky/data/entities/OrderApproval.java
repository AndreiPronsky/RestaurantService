package org.pronsky.data.entities;

import lombok.Data;

@Data
public class OrderApproval {
    private Long id;
    private OrderDetails orderDetails;
}
