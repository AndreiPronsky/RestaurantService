package org.pronsky.data.dao.impl;

import org.pronsky.data.dao.OrderApprovalDAO;
import org.pronsky.data.entities.OrderApproval;

import java.util.List;

public class OrderApprovalDAOImpl implements OrderApprovalDAO {
    @Override
    public OrderApproval getById(Long id) {
        return null;
    }

    @Override
    public List<OrderApproval> getAll() {
        return List.of();
    }

    @Override
    public OrderApproval create(OrderApproval approval) {
        return null;
    }

    @Override
    public OrderApproval update(OrderApproval approval) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }
}
