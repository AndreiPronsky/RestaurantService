package org.pronsky.data.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pronsky.data.connection.ConnectionUtil;
import org.pronsky.data.dao.OrderDetailsDAO;
import org.pronsky.data.entities.OrderDetails;
import org.pronsky.data.entities.Product;
import org.pronsky.exceptions.UnableToCreateException;
import org.pronsky.exceptions.UnableToDeleteException;
import org.pronsky.exceptions.UnableToFindException;
import org.pronsky.exceptions.UnableToUpdateException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for the OrderDetails entity using JDBC.
 * This class provides methods for database operations related to order details.
 */
@Log4j2
@RequiredArgsConstructor
public class OrderDetailsDAOImpl implements OrderDetailsDAO {
    private static final String CREATE_ORDER_DETAILS = "INSERT INTO order_details (status_id, total_amount) VALUES (?, ?)";
    private static final String CREATE_DETAILS_TO_PRODUCTS_RELATIONS = "INSERT INTO details_to_products (order_details_id, product_id) " +
            "VALUES (?, ?)";
    private static final String UPDATE_ORDER_DETAILS = "UPDATE order_details SET status_id = ?, total_amount = ?" +
            " WHERE id = ?";
    private static final String FIND_ORDER_DETAILS_BY_ID = "SELECT od.id, od.total_amount, os.name AS order_status " +
            "FROM order_details od " +
            "JOIN order_statuses os ON os.id = od.status_id " +
            "WHERE od.id = ?";
    private static final String FIND_ALL_ORDER_DETAILS = "SELECT od.id, od.total_amount, os.name AS order_status " +
            "FROM order_details od JOIN order_statuses os ON os.id = od.status_id ";
    private static final String DELETE_ORDER_DETAILS = "DELETE FROM order_details od WHERE od.id = ?";
    private static final String DELETE_DETAILS_TO_PRODUCT_RELATIONS = "DELETE FROM details_to_products dtp " +
            "WHERE dtp.order_details_id = ?";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TOTAL_AMOUNT = "total_amount";
    private final ConnectionUtil connectionUtil;

    /**
     * Retrieves order details by its ID from the database.
     *
     * @param id The ID of the order details to retrieve.
     * @return The order details with the specified ID.
     * @throws UnableToFindException If an error occurs during the retrieval process.
     */
    @Override
    public OrderDetails getById(Long id) {
        log.debug("OrderDetailsDAOImpl.getById");
        OrderDetails details = new OrderDetails();
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ORDER_DETAILS_BY_ID)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            setParameters(details, resultSet);
            log.debug("Fetched details : " + details);
            return details;
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    /**
     * Retrieves all order details from the database.
     *
     * @return A list of all order details.
     * @throws UnableToFindException If an error occurs during the retrieval process.
     */
    @Override
    public List<OrderDetails> getAll() {
        log.debug("OrderDetailsDAOImpl.getAll");
        List<OrderDetails> orderDetails = new ArrayList<>();
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_ORDER_DETAILS)) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                long id = result.getLong(COLUMN_ID);
                OrderDetails details = getById(id);
                orderDetails.add(details);
            }
            log.debug("fetched details : " + orderDetails);
            return orderDetails;
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    /**
     * Creates a new order details record in the database.
     *
     * @param details The order details object to create.
     * @return The created order details object with the generated ID.
     * @throws UnableToCreateException If an error occurs during the creation process.
     */
    @Override
    public OrderDetails create(OrderDetails details) {
        log.debug("OrderDetailsDAOImpl.create");
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_ORDER_DETAILS, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatementForCreate(details, statement);
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            if (result.next()) {
                details.setId(result.getLong(COLUMN_ID));
                setDetailsToProductRelations(details);
            }
            return getById(result.getLong(COLUMN_ID));
        } catch (SQLException e) {
            throw new UnableToCreateException(e);
        }
    }

    /**
     * Updates an existing order details record in the database.
     *
     * @param orderDetails The order details object to update.
     * @return The updated order details object.
     * @throws UnableToUpdateException If an error occurs during the update process.
     */
    @Override
    public OrderDetails update(OrderDetails orderDetails) {
        log.debug("OrderDetailsDAOImpl.update");
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_ORDER_DETAILS)) {
            prepareStatementForUpdate(orderDetails, statement);
            statement.executeUpdate();
            return getById(orderDetails.getId());
        } catch (SQLException e) {
            throw new UnableToUpdateException(e);
        }
    }

    /**
     * Deletes an order details record from the database by its ID.
     *
     * @param id The ID of the order details to delete.
     * @return True if the deletion was successful, false otherwise.
     * @throws UnableToDeleteException If an error occurs during the deletion process.
     */
    @Override
    public boolean deleteById(Long id) {
        log.debug("OrderDetailsDAOImpl.deleteById");
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_ORDER_DETAILS)) {
            deleteDetailsToProductRelations(id);
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new UnableToDeleteException(e);
        }
    }

    private void setParameters(OrderDetails details, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            details.setId(resultSet.getLong(COLUMN_ID));
            details.setOrderStatus(OrderDetails.OrderStatus.valueOf(resultSet.getString("order_status")));
            details.setTotalAmount(resultSet.getBigDecimal(COLUMN_TOTAL_AMOUNT));
        }
    }

    private void prepareStatementForCreate(OrderDetails details, PreparedStatement statement) throws SQLException {
        statement.setInt(1, details.getOrderStatus().ordinal() + 1);
        statement.setBigDecimal(2, details.getTotalAmount());
    }

    private void prepareStatementForUpdate(OrderDetails details, PreparedStatement statement) throws SQLException {
        statement.setInt(1, details.getOrderStatus().ordinal() + 1);
        statement.setBigDecimal(2, details.getTotalAmount());
        statement.setLong(3, details.getId());
    }

    private void setDetailsToProductRelations(OrderDetails details) {
        for (Product product : details.getProducts()) {
            try (Connection connection = connectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(CREATE_DETAILS_TO_PRODUCTS_RELATIONS)) {
                statement.setLong(1, details.getId());
                statement.setLong(2, product.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                log.error(e.getMessage());
                throw new UnableToCreateException("Unable to create relations", e);
            }
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
