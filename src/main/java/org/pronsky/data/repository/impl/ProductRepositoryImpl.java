package org.pronsky.data.repository.impl;

import lombok.RequiredArgsConstructor;
import org.pronsky.data.dao.ProductCategoryDAO;
import org.pronsky.data.dao.ProductDAO;
import org.pronsky.data.entities.Product;
import org.pronsky.data.repository.ProductRepository;

import java.util.List;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductDAO productDAO;
    private final ProductCategoryDAO categoryDAO;

    @Override
    public Product findById(Long id) {
        Product product = productDAO.getById(id);
        product.setProductCategories(categoryDAO.getAllByProductId(id));
        return product;
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = productDAO.getAll();
        products.forEach(product -> product.setProductCategories(categoryDAO.getAllByProductId(product.getId())));
        return products;
    }

    @Override
    public Product save(Product product) {
        Product saved;
        if (product.getId() != null) {
            saved = productDAO.update(product);
        } else {
            saved = productDAO.create(product);
        }
        return saved;
    }

    @Override
    public void delete(Product product) {
        productDAO.deleteById(product.getId());
    }
}
