package org.pronsky.data.dao.impl;

import org.pronsky.data.dao.ProductCategoryDAO;
import org.pronsky.data.entities.ProductCategory;
import org.pronsky.data.exceptions.UnableToCreateException;
import org.pronsky.data.exceptions.UnableToDeleteException;
import org.pronsky.data.exceptions.UnableToFindException;
import org.pronsky.data.exceptions.UnableToUpdateException;
import org.pronsky.utils.PropertyReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDAOImpl implements ProductCategoryDAO {
    private static final String CREATE_CATEGORY = "INSERT INTO product_categories (name, type) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_CATEGORY = "UPDATE product_categories SET name = ?, type = ? WHERE id = ?";
    private static final String FIND_CATEGORY_BY_ID = "SELECT pc.id, pc.name, pc.type " +
            "FROM product_categories pc WHERE pc.id = ?";
    private static final String FIND_ALL_CATEGORIES = "SELECT pc.id, pc.name, pc.type " +
            "FROM product_categories pc";
    private static final String DELETE_CATEGORY = "DELETE FROM product_categories pc WHERE pc.id = ?";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TYPE = "type";
    private static final PropertyReader propertyReader = PropertyReader.INSTANCE;
    private final String url = propertyReader.getUrl();
    private final String user = propertyReader.getUser();
    private final String password = propertyReader.getPassword();

    @Override
    public ProductCategory getById(Long id) {
        ProductCategory productCategory = new ProductCategory();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(FIND_CATEGORY_BY_ID)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            setParameters(productCategory, result);
            return productCategory;
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    @Override
    public List<ProductCategory> getAll() {
        List<ProductCategory> categories = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_CATEGORIES)) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                long id = result.getLong(COLUMN_ID);
                ProductCategory category = getById(id);
                categories.add(category);
            }
            return categories;
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    @Override
    public ProductCategory create(ProductCategory productCategory) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(CREATE_CATEGORY, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatementForCreate(productCategory, statement);
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            if (result.next()) {
                productCategory.setId(result.getLong(COLUMN_ID));
            }
            return getById(result.getLong(COLUMN_ID));
        } catch (SQLException e) {
            throw new UnableToCreateException(e);
        }
    }

    @Override
    public ProductCategory update(ProductCategory productCategory) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(UPDATE_CATEGORY)) {
            prepareStatementForUpdate(productCategory, statement);
            statement.executeUpdate();
            return getById(productCategory.getId());
        } catch (SQLException e) {
            throw new UnableToUpdateException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(DELETE_CATEGORY)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new UnableToDeleteException(e);
        }
    }

    private void setParameters(ProductCategory productCategory, ResultSet result) throws SQLException {
        while (result.next()) {
            productCategory.setId(result.getLong(COLUMN_ID));
            productCategory.setName(result.getString(COLUMN_NAME));
            productCategory.setType(ProductCategory.CategoryType.valueOf(result.getString(COLUMN_TYPE)));
        }
    }

    private void prepareStatementForCreate(ProductCategory category, PreparedStatement statement) throws SQLException {
        statement.setString(1, category.getName());
        statement.setString(2, category.getType().toString());
    }

    private void prepareStatementForUpdate(ProductCategory category, PreparedStatement statement) throws SQLException {
        statement.setString(1, category.getName());
        statement.setString(2, category.getType().toString());
        statement.setLong(3, category.getId());
    }
}
