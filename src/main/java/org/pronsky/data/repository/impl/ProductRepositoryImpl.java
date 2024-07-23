package org.pronsky.data.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pronsky.data.dao.ProductCategoryDAO;
import org.pronsky.data.dao.ProductDAO;
import org.pronsky.data.entities.Product;
import org.pronsky.data.repository.ProductRepository;

import java.util.List;


/**
 * Implementation of the ProductRepository interface.
 *
 * @author [Andrei Pronsky]
 */
@Log4j2
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductDAO productDAO;
    private final ProductCategoryDAO categoryDAO;

    /**
     * Finds a Product instance by its ID.
     *
     * @param id the ID of the Product instance to find
     * @return the found Product instance
     */
    @Override
    public Product findById(Long id) {
        Product product = productDAO.getById(id);
        product.setProductCategories(categoryDAO.getAllByProductId(id));
        log.debug("ProductRepositoryImpl : got product: " + product);
        return product;
    }

    /**
     * Finds all Product instances.
     *
     * @return a list of all Product instances
     */
    @Override
    public List<Product> findAll() {
        List<Product> products = productDAO.getAll();
        products.forEach(product -> product.setProductCategories(categoryDAO.getAllByProductId(product.getId())));
        log.debug("ProductRepositoryImpl : got products: " + products);
        return products;
    }

    /**
     * Saves a Product instance.
     *
     * @param product the Product instance to save
     * @return the saved Product instance
     */
    @Override
    public Product save(Product product) {
        Product saved;
        if (product.getId() != null) {
            saved = productDAO.update(product);
        } else {
            saved = productDAO.create(product);
        }
        log.debug("ProductRepositoryImpl : saved product: " + saved);
        return saved;
    }

    /**
     * Deletes a Product instance by its ID.
     *
     * @param id the ID of the Product instance to delete
     */
    @Override
    public void delete(Long id) {
        productDAO.deleteById(id);
        log.debug("ProductRepositoryImpl : deleted product: " + id);
    }
}
