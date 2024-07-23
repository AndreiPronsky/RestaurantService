package org.pronsky.data.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.pronsky.data.dao.OrderDetailsDAO;
import org.pronsky.data.dao.ProductCategoryDAO;
import org.pronsky.data.dao.ProductDAO;
import org.pronsky.data.entities.OrderDetails;
import org.pronsky.data.entities.Product;
import org.pronsky.data.repository.OrderDetailRepository;

import java.util.List;

/**
 * Implementation of the OrderDetailRepository interface.
 *
 * @author [Andrei Pronsky]
 */
@Log4j
@RequiredArgsConstructor
public class OrderDetailRepositoryImpl implements OrderDetailRepository {
    private final OrderDetailsDAO orderDetailsDAO;
    private final ProductDAO productDAO;
    private final ProductCategoryDAO productCategoryDAO;

    /**
     * Finds an OrderDetails instance by its ID.
     *
     * @param id the ID of the OrderDetails instance to find
     * @return the found OrderDetails instance
     */
    @Override
    public OrderDetails findById(Long id) {
        OrderDetails orderDetails = orderDetailsDAO.getById(id);
        List<Product> products = productDAO.getAllByOrderId(id);
        products.forEach(product -> product.setProductCategories(productCategoryDAO.getAllByProductId(product.getId())));
        orderDetails.setProducts(products);
        log.debug("OrderDetailRepositoryImpl: got order details: " + orderDetails);
        return orderDetails;
    }

    /**
     * Finds all OrderDetails instances.
     *
     * @return a list of all OrderDetails instances
     */
    @Override
    public List<OrderDetails> findAll() {
        List<OrderDetails> detailsList = orderDetailsDAO.getAll();
        detailsList.forEach(orderDetails -> {
            List<Product> products = productDAO.getAllByOrderId(orderDetails.getId());
            products.forEach(product -> product.setProductCategories(productCategoryDAO.getAllByProductId(product.getId())));
            orderDetails.setProducts(products);
        });
        log.debug("OrderDetailRepositoryImpl: got order details: " + detailsList);
        return detailsList;
    }

    /**
     * Saves an OrderDetails instance.
     *
     * @param orderDetails the OrderDetails instance to save
     * @return the saved OrderDetails instance
     */
    @Override
    public OrderDetails save(OrderDetails orderDetails) {
        OrderDetails saved;
        if (orderDetails.getId() == null) {
            saved = orderDetailsDAO.create(orderDetails);
        } else {
            saved = orderDetailsDAO.update(orderDetails);
        }
        log.debug("OrderDetailRepositoryImpl: saved order details: " + saved);
        return saved;
    }

    /**
     * Deletes an OrderDetails instance by its ID.
     *
     * @param id the ID of the OrderDetails instance to delete
     */
    @Override
    public void delete(Long id) {
        orderDetailsDAO.deleteById(id);
        log.debug("OrderDetailRepositoryImpl: deleted order details: " + id);
    }
}
