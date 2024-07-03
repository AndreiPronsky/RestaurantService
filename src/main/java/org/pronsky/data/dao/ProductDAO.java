package org.pronsky.data.dao;

import org.pronsky.data.entities.Product;

import java.util.List;

public interface ProductDAO extends AbstractDAO<Long, Product> {
    List<Product> getAllByOrderId(long orderId);
}
