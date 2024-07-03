package org.pronsky.data.repository.impl;

import lombok.RequiredArgsConstructor;
import org.pronsky.data.dao.OrderDetailsDAO;
import org.pronsky.data.dao.ProductCategoryDAO;
import org.pronsky.data.dao.ProductDAO;
import org.pronsky.data.entities.OrderDetails;
import org.pronsky.data.entities.Product;
import org.pronsky.data.repository.OrderDetailRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderDetailRepositoryImpl implements OrderDetailRepository {
    private final OrderDetailsDAO orderDetailsDAO;
    private final ProductDAO productDAO;
    private final ProductCategoryDAO productCategoryDAO;

    @Override
    public OrderDetails findById(long id) {
        OrderDetails orderDetails = orderDetailsDAO.getById(id);
        List<Product> products = productDAO.getAllByOrderId(id);
        products.forEach(product -> product.setProductCategories(productCategoryDAO.getAllByProductId(product.getId())));
        orderDetails.setProducts(products);
        return orderDetails;
    }

    @Override
    public List<OrderDetails> findAll() {
        List<OrderDetails> detailsList = orderDetailsDAO.getAll();
        detailsList.forEach(orderDetails -> {
            List<Product> products = productDAO.getAllByOrderId(orderDetails.getId()).stream()
                    .peek(product -> product.setProductCategories(productCategoryDAO.getAllByProductId(product.getId())))
                    .collect(Collectors.toList());
            orderDetails.setProducts(products);
        });
        return detailsList;
    }

    @Override
    public void save(OrderDetails orderDetails) {
        if (orderDetails.getId() == null) {
            orderDetailsDAO.create(orderDetails);
        } else {
            orderDetailsDAO.update(orderDetails);
        }
    }

    @Override
    public void delete(OrderDetails orderDetails) {
        orderDetailsDAO.deleteById(orderDetails.getId());
    }
}
