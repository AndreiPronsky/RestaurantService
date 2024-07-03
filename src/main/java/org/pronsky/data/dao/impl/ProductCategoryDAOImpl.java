package org.pronsky.data.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.pronsky.data.dao.ProductCategoryDAO;
import org.pronsky.data.entities.ProductCategory;
import org.pronsky.data.entities.ProductCategory.CategoryType;
import org.pronsky.data.exceptions.UnableToCreateException;
import org.pronsky.data.exceptions.UnableToDeleteException;
import org.pronsky.data.exceptions.UnableToFindException;
import org.pronsky.data.exceptions.UnableToUpdateException;
import org.pronsky.utils.PropertyReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ProductCategoryDAOImpl implements ProductCategoryDAO {
    private static final String CREATE_CATEGORY = "INSERT INTO product_categories (name, category_type) " +
            "VALUES (?, ?)";
    private static final String CREATE_CATEGORY_TO_TYPE_RELATION = "INSERT INTO categories_to_types " +
            "(category_id, type_id) VALUES (?, ?)";
    private static final String UPDATE_CATEGORY = "UPDATE product_categories SET name = ?, category_type = ? " +
            "WHERE id = ?";
    private static final String FIND_CATEGORY_BY_ID = "SELECT pc.id, pc.name, ct.name AS type_name " +
            "FROM product_categories pc " +
            "JOIN categories_to_types ctt ON ctt.category_id = pc.id " +
            "JOIN category_types ct ON pc.id = ctt.category_id WHERE pc.id = ?";
    private static final String FIND_ALL_BY_PRODUCT_ID = "SELECT p.name, pc.name AS category_name, ct.name AS type_name " +
            "FROM product_categories pc " +
            "JOIN product_to_category ptc ON ptc.category_id = pc.id " +
            "JOIN products p ON p.id = ptc.product_id " +
            "JOIN categories_to_types ctt ON ptc.category_id = ctt.category_id " +
            "JOIN category_types ct ON ct.id = ctt.type_id " +
            "WHERE p.id = ?";
    private static final String FIND_ALL_CATEGORIES = "SELECT pc.id, pc.name, ct.name AS type_name " +
            "FROM product_categories pc " +
            "JOIN category_types ct ON ct.id = pc.category_type ";
    private static final String DELETE_CATEGORY = "DELETE FROM product_categories pc WHERE pc.id = ?";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TYPE = "type_name";
    private static final PropertyReader propertyReader = PropertyReader.INSTANCE;
    private final String url = propertyReader.getUrl();
    private final String user = propertyReader.getUser();
    private final String password = propertyReader.getPassword();

    @Override
    public ProductCategory getById(Long id) {
        log.debug("ProductCategoryDAOImpl.getById");
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
        log.debug("ProductCategoryDAOImpl.getAll");
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
        log.debug("ProductCategoryDAOImpl.create");
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(CREATE_CATEGORY, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatementForCreate(productCategory, statement);
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            if (result.next()) {
                productCategory.setId(result.getLong(COLUMN_ID));
                createCategoryToTypeRelation(productCategory, connection);
            }
            return getById(result.getLong(COLUMN_ID));
        } catch (SQLException e) {
            throw new UnableToCreateException(e);
        }
    }

    @Override
    public ProductCategory update(ProductCategory productCategory) {
        log.debug("ProductCategoryDAOImpl.update");
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
        log.debug("ProductCategoryDAOImpl.deleteById");
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(DELETE_CATEGORY)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new UnableToDeleteException(e);
        }
    }

    @Override
    public List<ProductCategory> getAllByProductId(Long productId) {
        log.debug("ProductCategoryDAOImpl.getAllByProductId");
        Map<Long, ProductCategory> productCategoryMap = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_PRODUCT_ID)) {
            statement.setLong(1, productId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(COLUMN_ID);
                String name = resultSet.getString(COLUMN_NAME);
                String typeName = resultSet.getString(COLUMN_TYPE);
                CategoryType categoryType = CategoryType.valueOf(typeName);
                ProductCategory productCategory = new ProductCategory();
                productCategory.setId(id);
                productCategory.setName(name);
                productCategoryMap.put(id, productCategory);
                productCategory.getTypes().add(categoryType);
            }
            return new ArrayList<>(productCategoryMap.values());
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    private void createCategoryToTypeRelation(ProductCategory category, Connection connection) throws SQLException {
        for (CategoryType type : category.getTypes()) {
            PreparedStatement statement = connection.prepareStatement(CREATE_CATEGORY_TO_TYPE_RELATION);
            statement.setLong(1, category.getId());
            statement.setLong(2, type.ordinal());
        }
    }

    private void setParameters(ProductCategory productCategory, ResultSet result) throws SQLException {
        while (result.next()) {
            productCategory.setId(result.getLong(COLUMN_ID));
            productCategory.setName(result.getString(COLUMN_NAME));
        }
    }

    private void prepareStatementForCreate(ProductCategory category, PreparedStatement statement) throws SQLException {
        statement.setString(1, category.getName());
    }

    private void prepareStatementForUpdate(ProductCategory category, PreparedStatement statement) throws SQLException {
        statement.setString(1, category.getName());
        statement.setLong(3, category.getId());
    }
}
