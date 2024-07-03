package org.pronsky.data.repository;

import org.pronsky.data.entities.OrderDetails;

import java.util.List;

public interface OrderDetailRepository {
    OrderDetails findById(long id);

    List<OrderDetails> findAll();

    void save(OrderDetails orderDetails);

    void delete(OrderDetails orderDetails);
}
