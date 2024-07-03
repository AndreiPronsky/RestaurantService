package org.pronsky.data.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.pronsky.data.dao.ProductDAO;
import org.pronsky.data.entities.Product;
import org.pronsky.data.entities.ProductCategory;
import org.pronsky.data.exceptions.UnableToCreateException;
import org.pronsky.data.exceptions.UnableToDeleteException;
import org.pronsky.data.exceptions.UnableToFindException;
import org.pronsky.data.exceptions.UnableToUpdateException;
import org.pronsky.utils.PropertyReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProductDAOImpl implements ProductDAO {

    private static final String CREATE_PRODUCT = "INSERT INTO products (name, price, quantity, available) " +
            "VALUES (?, ?, ?, ?)";
    private static final String CREATE_PRODUCT_TO_CATEGORY_RELATION = "INSERT INTO product_to_category " +
            "(product_id, category_id) VALUES (?, ?)";
    private static final String UPDATE_PRODUCT = "UPDATE products SET name = ?, price = ?, quantity = ?, available = ? " +
            "WHERE id = ?";
    private static final String FIND_PRODUCT_BY_ID = "SELECT p.id, p.name, p.price, p.quantity, p.available " +
            "FROM products p WHERE p.id = ?";
    private static final String FIND_ALL_PRODUCTS = "SELECT p.id, p.name, p.price, p.quantity, p.available " +
            "FROM products p";
    private static final String FIND_ALL_BY_ORDER_ID = "SELECT p.id, p.name, p.price, p.quantity, p.available " +
            "FROM products p " +
            "JOIN details_to_products dtp on p.id = dtp.product_id " +
            "WHERE dtp.order_details_id = ?";
    private static final String DELETE_PRODUCT = "DELETE FROM products p WHERE p.id = ?";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_AVAILABLE = "available";
    private static final PropertyReader propertyReader = PropertyReader.INSTANCE;
    private final String url = propertyReader.getUrl();
    private final String user = propertyReader.getUser();
    private final String password = propertyReader.getPassword();

    @Override
    public Product getById(Long id) {
        log.debug("ProductDAOImpl.getById");
        Product product = new Product();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(FIND_PRODUCT_BY_ID)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            setParameters(product, result);
            return product;
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    @Override
    public List<Product> getAll() {
        log.debug("ProductDAOImpl.getAll");
        List<Product> products = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_PRODUCTS)) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                long id = result.getLong(COLUMN_ID);
                Product product = getById(id);
                products.add(product);
            }
            return products;
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    @Override
    public Product create(Product product) {
        log.debug("ProductDAOImpl.create");
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(CREATE_PRODUCT, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatementForCreate(product, statement);
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            if (result.next()) {
                product.setId(result.getLong(COLUMN_ID));
                setProductToCategoryRelations(product, connection);
            }
            return getById(result.getLong(COLUMN_ID));
        } catch (SQLException e) {
            throw new UnableToCreateException(e);
        }
    }

    @Override
    public Product update(Product product) {
        log.debug("ProductDAOImpl.update");
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(UPDATE_PRODUCT)) {
            prepareStatementForUpdate(product, statement);
            statement.executeUpdate();
            return getById(product.getId());
        } catch (SQLException e) {
            throw new UnableToUpdateException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        log.debug("ProductDAOImpl.deleteById");
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(DELETE_PRODUCT)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new UnableToDeleteException(e);
        }
    }

    @Override
    public List<Product> getAllByOrderId(long orderId) {
        log.debug("ProductDAOImpl.getAllByOrderId");
        List<Product> products = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_ORDER_ID)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                setParameters(product, resultSet);
                products.add(product);
            }
            return products;
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    private void prepareStatementForCreate(Product product, PreparedStatement statement) throws SQLException {
        statement.setString(1, product.getName());
        statement.setString(2, String.valueOf(product.getPrice()));
        statement.setInt(3, product.getQuantity());
        statement.setBoolean(4, product.isAvailable());
    }

    private void prepareStatementForUpdate(Product product, PreparedStatement statement) throws SQLException {
        statement.setString(1, product.getName());
        statement.setString(2, String.valueOf(product.getPrice()));
        statement.setInt(3, product.getQuantity());
        statement.setBoolean(4, product.isAvailable());
        statement.setLong(5, product.getId());
    }

    private void setParameters(Product product, ResultSet result) throws SQLException {
        while (result.next()) {
            product.setId(result.getLong(COLUMN_ID));
            product.setName(result.getString(COLUMN_NAME));
            product.setPrice(result.getBigDecimal(COLUMN_PRICE));
            product.setQuantity(result.getInt(COLUMN_QUANTITY));
            product.setAvailable(result.getBoolean(COLUMN_AVAILABLE));
        }
    }

    private void setProductToCategoryRelations(Product product, Connection connection) throws SQLException {
        for (ProductCategory category : product.getProductCategories()) {
            PreparedStatement categoryStatement = connection.prepareStatement(CREATE_PRODUCT_TO_CATEGORY_RELATION);
            categoryStatement.setLong(1, product.getId());
            categoryStatement.setLong(2, category.getId());
        }
    }
}
