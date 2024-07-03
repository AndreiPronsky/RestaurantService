package org.pronsky.data.repository;

import org.pronsky.data.entities.Product;

import java.util.List;

public interface ProductRepository {
    Product findById(long id);

    List<Product> findAll();

    void save(Product product);

    void delete(Product product);
}
