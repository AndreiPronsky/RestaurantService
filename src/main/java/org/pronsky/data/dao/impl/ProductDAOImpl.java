package org.pronsky.data.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pronsky.data.connection.ConnectionUtil;
import org.pronsky.data.dao.ProductDAO;
import org.pronsky.data.entities.Product;
import org.pronsky.data.entities.ProductCategory;
import org.pronsky.exceptions.UnableToCreateException;
import org.pronsky.exceptions.UnableToDeleteException;
import org.pronsky.exceptions.UnableToFindException;
import org.pronsky.exceptions.UnableToUpdateException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for the Product entity using JDBC.
 * This class provides methods for database operations related to products.
 */
@Log4j2
@RequiredArgsConstructor
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
    private static final String DELETE_PRODUCT_TO_CATEGORY_RELATIONS = "DELETE FROM product_to_category ptc " +
            "WHERE ptc.product_id = ?";
    private static final String DELETE_DETAILS_TO_PRODUCT_RELATIONS = "DELETE FROM details_to_products dtp " +
            "WHERE dtp.product_id = ?";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_AVAILABLE = "available";
    private final ConnectionUtil connectionUtil;

    /**
     * Retrieves a product by its ID from the database.
     *
     * @param id The ID of the product to retrieve.
     * @return The product with the specified ID.
     * @throws UnableToFindException If an error occurs during the retrieval process.
     */
    @Override
    public Product getById(Long id) {
        log.debug("ProductDAOImpl.getById");
        Product product = new Product();
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_PRODUCT_BY_ID)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            product.setId(resultSet.getLong(COLUMN_ID));
            product.setName(resultSet.getString(COLUMN_NAME));
            product.setPrice(resultSet.getBigDecimal(COLUMN_PRICE));
            product.setQuantity(resultSet.getInt(COLUMN_QUANTITY));
            product.setAvailable(resultSet.getBoolean(COLUMN_AVAILABLE));
            log.debug("Fetched product : " + product);
            return product;
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    /**
     * Retrieves all products from the database.
     *
     * @return A list of all products.
     * @throws UnableToFindException If an error occurs during the retrieval process.
     */
    @Override
    public List<Product> getAll() {
        log.debug("ProductDAOImpl.getAll");
        List<Product> products = new ArrayList<>();
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_PRODUCTS)) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                long id = result.getLong(COLUMN_ID);
                Product product = getById(id);
                products.add(product);
            }
            log.debug("Fetched products : " + products);
            return products;
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    /**
     * Creates a new product record in the database.
     *
     * @param product The product object to create.
     * @return The created product object with the generated ID.
     * @throws UnableToCreateException If an error occurs during the creation process.
     */
    @Override
    public Product create(Product product) {
        log.debug("ProductDAOImpl.create");
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_PRODUCT, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatementForCreate(product, statement);
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            if (result.next()) {
                product.setId(result.getLong(COLUMN_ID));
                setProductToCategoryRelations(product);
            }
            return product;
        } catch (SQLException e) {
            throw new UnableToCreateException(e);
        }
    }

    /**
     * Updates an existing product record in the database.
     *
     * @param product The product object to update.
     * @return The updated product object.
     * @throws UnableToUpdateException If an error occurs during the update process.
     */
    @Override
    public Product update(Product product) {
        log.debug("ProductDAOImpl.update");
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_PRODUCT)) {
            prepareStatementForUpdate(product, statement);
            statement.executeUpdate();
            return product;
        } catch (SQLException e) {
            throw new UnableToUpdateException(e);
        }
    }

    /**
     * Deletes a product record from the database by its ID.
     *
     * @param id The ID of the product to delete.
     * @return True if the deletion was successful, false otherwise.
     * @throws UnableToDeleteException If an error occurs during the deletion process.
     */
    @Override
    public boolean deleteById(Long id) {
        log.debug("ProductDAOImpl.deleteById");
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_PRODUCT)) {
            boolean categoryRelationsAreDeleted = deleteProductToCategoryRelations(id);
            boolean detailRelationsAreDeleted = deleteDetailsToProductRelations(id);
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return (affectedRows == 1 && detailRelationsAreDeleted && categoryRelationsAreDeleted);
        } catch (SQLException e) {
            throw new UnableToDeleteException(e);
        }
    }

    /**
     * Retrieves all products associated with a specific order ID.
     *
     * @param orderId The ID of the order.
     * @return A list of products associated with the order.
     * @throws UnableToFindException If an error occurs during the retrieval process.
     */
    @Override
    public List<Product> getAllByOrderId(long orderId) {
        log.debug("ProductDAOImpl.getAllByOrderId");
        List<Product> products = new ArrayList<>();
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_ORDER_ID)) {
            statement.setLong(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getLong(COLUMN_ID));
                product.setName(resultSet.getString(COLUMN_NAME));
                product.setPrice(resultSet.getBigDecimal(COLUMN_PRICE));
                product.setQuantity(resultSet.getInt(COLUMN_QUANTITY));
                product.setAvailable(resultSet.getBoolean(COLUMN_AVAILABLE));
                products.add(product);
            }
            return products;
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    private void prepareStatementForCreate(Product product, PreparedStatement statement) throws SQLException {
        statement.setString(1, product.getName());
        statement.setBigDecimal(2, product.getPrice());
        statement.setInt(3, product.getQuantity());
        statement.setBoolean(4, product.isAvailable());
    }

    private void prepareStatementForUpdate(Product product, PreparedStatement statement) throws SQLException {
        statement.setString(1, product.getName());
        statement.setBigDecimal(2, product.getPrice());
        statement.setInt(3, product.getQuantity());
        statement.setBoolean(4, product.isAvailable());
        statement.setLong(5, product.getId());
    }

    private void setProductToCategoryRelations(Product product) throws SQLException {
        for (ProductCategory category : product.getProductCategories()) {
            try (Connection connection = connectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(CREATE_PRODUCT_TO_CATEGORY_RELATION)) {
                statement.setLong(1, product.getId());
                statement.setLong(2, category.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new UnableToCreateException("Unable to create relations", e);
            }
        }
    }

    private boolean deleteProductToCategoryRelations(Long id) throws SQLException {
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_PRODUCT_TO_CATEGORY_RELATIONS)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows >= 1;
        }
    }

    private boolean deleteDetailsToProductRelations(Long id) throws SQLException {
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_DETAILS_TO_PRODUCT_RELATIONS)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows >= 1;
        }
    }
}
