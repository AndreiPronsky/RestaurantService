package org.pronsky.data.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.pronsky.data.dao.OrderDetailsDAO;
import org.pronsky.data.entities.OrderDetails;
import org.pronsky.data.exceptions.UnableToCreateException;
import org.pronsky.data.exceptions.UnableToDeleteException;
import org.pronsky.data.exceptions.UnableToFindException;
import org.pronsky.data.exceptions.UnableToUpdateException;
import org.pronsky.utils.PropertyReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OrderDetailsDAOImpl implements OrderDetailsDAO {
    private static final String CREATE_ORDER_DETAILS = "INSERT INTO order_details (status_id, total_amount) VALUES (?, ?)";
    private static final String UPDATE_ORDER_DETAILS = "UPDATE order_details SET status_id = ?, total_amount = ?" +
            " WHERE id = ?";
    private static final String FIND_ORDER_DETAILS_BY_ID = "SELECT od.id, od.total_amount, os.name AS order_status " +
            "FROM order_details od " +
            "JOIN order_statuses os ON os.id = od.status_id " +
            "WHERE od.id = ?";
    private static final String FIND_ALL_ORDER_DETAILS = "SELECT od.id, od.total_amount, os.name AS order_status " +
            "FROM order_details od JOIN order_statuses os ON os.id = od.status_id ";
    private static final String DELETE_ORDER_DETAILS = "DELETE FROM order_details od WHERE od.id = ?";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_STATUS = "status_id";
    private static final String COLUMN_TOTAL_AMOUNT = "total_amount";
    private static final PropertyReader propertyReader = PropertyReader.INSTANCE;
    private final String url = propertyReader.getUrl();
    private final String user = propertyReader.getUser();
    private final String password = propertyReader.getPassword();

    @Override
    public OrderDetails getById(Long id) {
        log.debug("OrderDetailsDAOImpl.getById");
        OrderDetails details = new OrderDetails();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ORDER_DETAILS_BY_ID)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            setParameters(details, resultSet);
            return details;
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    @Override
    public List<OrderDetails> getAll() {
        log.debug("OrderDetailsDAOImpl.getAll");
        List<OrderDetails> orderDetails = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_ORDER_DETAILS)) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                long id = result.getLong(COLUMN_ID);
                OrderDetails details = getById(id);
                orderDetails.add(details);
            }
            return orderDetails;
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    @Override
    public OrderDetails create(OrderDetails details) {
        log.debug("OrderDetailsDAOImpl.create");
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(CREATE_ORDER_DETAILS, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatementForCreate(details, statement);
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            if (result.next()) {
                details.setId(result.getLong(COLUMN_ID));
            }
            return getById(result.getLong(COLUMN_ID));
        } catch (SQLException e) {
            throw new UnableToCreateException(e);
        }
    }

    @Override
    public OrderDetails update(OrderDetails orderDetails) {
        log.debug("OrderDetailsDAOImpl.update");
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(UPDATE_ORDER_DETAILS)) {
            prepareStatementForUpdate(orderDetails, statement);
            statement.executeUpdate();
            return getById(orderDetails.getId());
        } catch (SQLException e) {
            throw new UnableToUpdateException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        log.debug("OrderDetailsDAOImpl.deleteById");
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(DELETE_ORDER_DETAILS)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new UnableToDeleteException(e);
        }
    }

    private void setParameters(OrderDetails details, ResultSet resultSet) throws SQLException {
        details.setId(resultSet.getLong(COLUMN_ID));
        details.setOrderStatus(OrderDetails.OrderStatus.valueOf(resultSet.getString(COLUMN_STATUS)));
        details.setTotalAmount(resultSet.getBigDecimal(COLUMN_TOTAL_AMOUNT));
    }

    private void prepareStatementForCreate(OrderDetails details, PreparedStatement statement) throws SQLException {
        statement.setString(1, String.valueOf(details.getOrderStatus()));
        statement.setString(2, String.valueOf(details.getTotalAmount()));
    }

    private void prepareStatementForUpdate(OrderDetails details, PreparedStatement statement) throws SQLException {
        statement.setString(1, String.valueOf(details.getOrderStatus()));
        statement.setString(2, String.valueOf(details.getTotalAmount()));
        statement.setLong(3, details.getId());
    }
}
