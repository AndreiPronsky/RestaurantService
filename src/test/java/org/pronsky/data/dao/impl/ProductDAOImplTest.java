package org.pronsky.data.dao.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pronsky.data.connection.ConnectionUtil;
import org.pronsky.data.dao.ProductDAO;
import org.pronsky.data.entities.Product;
import org.pronsky.data.entities.ProductCategory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class ProductDAOImplTest {

    private static final Long ID = 1L;
    private static final String FIND_PRODUCT_BY_ID = "SELECT p.id, p.name, p.price, p.quantity, p.available " +
            "FROM products p WHERE p.id = ?";
    private static ConnectionUtil connectionUtil;
    private static ProductDAO productDAO;
    private static Connection connection;
    private static Product newProduct;
    private static Product existingProduct;
    private static ProductCategory category;
    private static ProductCategory anotherCategory;
    private static ProductCategory existingCategory;
    private static Set<ProductCategory> categories;

    @Container
    public PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    @BeforeAll
    static void setUpBeforeClass() {
        connectionUtil = new ConnectionUtil();
        connection = connectionUtil.getConnection();

        Set<ProductCategory.CategoryType> types = new HashSet<>();
        types.add(ProductCategory.CategoryType.PERISHABLE);
        types.add(ProductCategory.CategoryType.FRIDGE_STORAGE);

        category = new ProductCategory();
        category.setId(1L);
        category.setName("category");
        category.setTypes(types);

        existingCategory = new ProductCategory();
        existingCategory.setId(2L);
        existingCategory.setName("MEAT");

        anotherCategory = new ProductCategory();
        anotherCategory.setId(2L);
        anotherCategory.setName("Another category");
        anotherCategory.setTypes(types);

        categories = new HashSet<>();
        categories.add(category);
        categories.add(anotherCategory);

        newProduct = new Product();
        newProduct.setName("New product 3");
        newProduct.setPrice(new BigDecimal("55.99"));
        newProduct.setQuantity(10);
        newProduct.setAvailable(true);
        newProduct.setProductCategories(categories);

        existingProduct = new Product();
        existingProduct.setName("Beef tenderloin");
        existingProduct.setPrice(new BigDecimal(10));
        existingProduct.setQuantity(3);
        existingProduct.setAvailable(true);
        existingProduct.setProductCategories(new HashSet<>());
    }

    @BeforeEach
    void setUp() {
        productDAO = new ProductDAOImpl(connectionUtil);
    }

    @Test
    void testCreateProduct() throws SQLException {
        Product created = productDAO.create(newProduct);

        PreparedStatement statement = connection.prepareStatement(FIND_PRODUCT_BY_ID);
        statement.setLong(1, created.getId());
        ResultSet resultSet = statement.executeQuery();

        assertTrue(resultSet.next());
        assertEquals(created.getId(), resultSet.getLong("id"));
        assertEquals(newProduct.getName(), resultSet.getString("name"));
        assertEquals(newProduct.getPrice(), resultSet.getBigDecimal("price"));
        assertEquals(newProduct.getQuantity(), resultSet.getInt("quantity"));
        assertEquals(newProduct.isAvailable(), resultSet.getBoolean("available"));
    }

    @Test
    void testUpdateProduct() throws SQLException {
        Product existing = productDAO.getById(ID);
        existing.setName("Update Product");
        existing.setPrice(new BigDecimal("19.99"));
        existing.setQuantity(10);
        existing.setAvailable(true);

        productDAO.update(existing);

        PreparedStatement statement = connection.prepareStatement(FIND_PRODUCT_BY_ID);
        statement.setLong(1, existing.getId());
        ResultSet resultSet = statement.executeQuery();

        assertTrue(resultSet.next());
        assertEquals("Update Product", resultSet.getString("name"));
        assertEquals(new BigDecimal("19.99"), resultSet.getBigDecimal("price"));
        assertEquals(10, resultSet.getInt("quantity"));
        assertTrue(resultSet.getBoolean("available"));
    }

    @Test
    void testGetProductById() {
        Product actual = productDAO.getById(ID);

        assertEquals("Update Product", actual.getName());
        assertEquals(BigDecimal.valueOf(19.99), actual.getPrice());
        assertEquals(10, actual.getQuantity());
        assertTrue(actual.isAvailable());
    }

    @Test
    void testGetAllProducts() {
        List<Product> actual = productDAO.getAll();
        assertEquals(32, actual.size());
    }

    @Test
    void testDeleteProduct() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(FIND_PRODUCT_BY_ID);
        statement.setLong(1, 31L);
        ResultSet resultSet = statement.executeQuery();
        assertFalse(resultSet.next());
    }
}