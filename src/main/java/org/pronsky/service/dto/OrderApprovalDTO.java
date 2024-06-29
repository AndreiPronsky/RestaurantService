package org.pronsky.service.dto;

import lombok.Data;
import org.pronsky.data.entities.OrderDetails;

@Data
public class OrderApprovalDTO {
    private Long id;
    private OrderDetails orderDetails;
}
