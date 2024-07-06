package org.pronsky.controller.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.factory.Mappers;
import org.pronsky.controller.Controller;
import org.pronsky.controller.impl.OrderDetailsController;
import org.pronsky.controller.impl.ProductController;
import org.pronsky.data.connection.ConnectionUtil;
import org.pronsky.data.dao.OrderDetailsDAO;
import org.pronsky.data.dao.ProductCategoryDAO;
import org.pronsky.data.dao.ProductDAO;
import org.pronsky.data.dao.impl.OrderDetailsDAOImpl;
import org.pronsky.data.dao.impl.ProductCategoryDAOImpl;
import org.pronsky.data.dao.impl.ProductDAOImpl;
import org.pronsky.data.repository.OrderDetailRepository;
import org.pronsky.data.repository.ProductRepository;
import org.pronsky.data.repository.impl.OrderDetailRepositoryImpl;
import org.pronsky.data.repository.impl.ProductRepositoryImpl;
import org.pronsky.service.OrderDetailsService;
import org.pronsky.service.ProductService;
import org.pronsky.service.impl.OrderDetailsServiceImpl;
import org.pronsky.service.impl.ProductServiceImpl;
import org.pronsky.service.mapper.Mapper;

import java.util.HashMap;
import java.util.Map;

public class ControllerFactory {

    public static final ControllerFactory INSTANCE = new ControllerFactory();
    private final Map<String, Controller> controllers;

    private ControllerFactory() {
        ConnectionUtil connectionUtil = new ConnectionUtil();
        OrderDetailsDAO orderDetailsDAO = new OrderDetailsDAOImpl(connectionUtil);
        ProductCategoryDAO productCategoryDAO = new ProductCategoryDAOImpl(connectionUtil);
        ProductDAO productDAO = new ProductDAOImpl(connectionUtil);
        OrderDetailRepository orderDetailRepository = new OrderDetailRepositoryImpl(orderDetailsDAO, productDAO, productCategoryDAO);
        ProductRepository productRepository = new ProductRepositoryImpl(productDAO, productCategoryDAO);
        Mapper mapper = Mappers.getMapper(Mapper.class);
        OrderDetailsService orderDetailsService = new OrderDetailsServiceImpl(mapper, orderDetailRepository);
        ProductService productService = new ProductServiceImpl(mapper, productRepository);
        ObjectMapper objectMapper = new ObjectMapper();
        OrderDetailsController orderDetailsController = new OrderDetailsController(orderDetailsService, objectMapper);
        ProductController productController = new ProductController(productService, objectMapper);
        controllers = new HashMap<>();
        controllers.put("products", productController);
        controllers.put("order_details", orderDetailsController);
    }

    public Controller getController(String controllerName) {
        return controllers.get(controllerName);
    }
}
