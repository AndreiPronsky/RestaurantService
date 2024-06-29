package org.pronsky.data.dao.impl;

import lombok.extern.log4j.Log4j;
import org.pronsky.data.dao.ProductDAO;
import org.pronsky.data.entities.Product;
import org.pronsky.data.exceptions.UnableToCreateException;
import org.pronsky.data.exceptions.UnableToDeleteException;
import org.pronsky.data.exceptions.UnableToFindException;
import org.pronsky.data.exceptions.UnableToUpdateException;
import org.pronsky.utils.PropertyReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Log4j
public class ProductDAOImpl implements ProductDAO {

    private static final String CREATE_PRODUCT = "INSERT INTO products (name, price, quantity, available) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_PRODUCT = "UPDATE products SET name = :name, price = :price, " +
            "quantity = :quantity, available = :available WHERE id = :id";
    private static final String FIND_PRODUCT_BY_ID = "SELECT p.id, p.name, p.price, p.quantity, p.available " +
            "FROM products p WHERE b.id = ?";
    private static final String FIND_ALL_PRODUCTS = "SELECT p.id, p.name, p.price, p.quantity, p.available " +
            "FROM products p";
    private static final String DELETE_PRODUCT = "DELETE FROM products p WHERE p.id = ?";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_AVAILABLE = "available";
    private static final PropertyReader propertyReader = PropertyReader.INSTANCE;
    private final String url = propertyReader.getUrl();
    private final String password = propertyReader.getPassword();
    private final String user = propertyReader.getUser();

    @Override
    public Product getById(Long id) {
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
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(CREATE_PRODUCT, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatementForCreate(product, statement);
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            if (result.next()) {
                product.setId(result.getLong(COLUMN_ID));
            }
            return getById(result.getLong(COLUMN_ID));
        } catch (SQLException e) {
            throw new UnableToCreateException(e);
        }
    }

    @Override
    public Product update(Product product) {
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
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(DELETE_PRODUCT)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new UnableToDeleteException(e);
        }
    }

    private void prepareStatementForCreate(Product product, PreparedStatement statement) throws SQLException {
        statement.setString(1, product.getName());
        statement.setString(2, product.getPrice().toString());
        statement.setInt(3, product.getQuantity());
        statement.setBoolean(4, product.isAvailable());
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

    private void prepareStatementForUpdate(Product product, PreparedStatement statement) throws SQLException {
        statement.setString(1, product.getName());
        statement.setString(2, product.getPrice().toString());
        statement.setInt(3, product.getQuantity());
        statement.setBoolean(4, product.isAvailable());
        statement.setLong(5, product.getId());
    }
}
