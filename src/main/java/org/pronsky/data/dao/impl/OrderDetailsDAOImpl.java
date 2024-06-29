package org.pronsky.data.dao.impl;

import org.pronsky.data.dao.OrderDetailsDAO;
import org.pronsky.data.entities.OrderDetails;

import java.util.List;

public class OrderDetailsDAOImpl implements OrderDetailsDAO {
    @Override
    public OrderDetails getById(Long id) {
        return null;
    }

    @Override
    public List<OrderDetails> getAll() {
        return List.of();
    }

    @Override
    public OrderDetails create(OrderDetails orderDetails) {
        return null;
    }

    @Override
    public OrderDetails update(OrderDetails orderDetails) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }
}
