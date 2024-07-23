package org.pronsky.data.dao;

import org.pronsky.data.entities.ProductCategory;

import java.util.Set;

public interface ProductCategoryDAO extends AbstractDAO<Long, ProductCategory> {
    Set<ProductCategory> getAllByProductId(Long productId);
}
