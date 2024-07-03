package org.pronsky.data.dao;

import org.pronsky.data.entities.ProductCategory;

import java.util.List;

public interface ProductCategoryDAO extends AbstractDAO<Long, ProductCategory> {
    List<ProductCategory> getAllByProductId(Long productId);
}
