package org.pronsky.data.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pronsky.data.connection.ConnectionUtil;
import org.pronsky.data.dao.ProductCategoryDAO;
import org.pronsky.data.entities.ProductCategory;
import org.pronsky.data.entities.ProductCategory.CategoryType;
import org.pronsky.exceptions.UnableToCreateException;
import org.pronsky.exceptions.UnableToDeleteException;
import org.pronsky.exceptions.UnableToFindException;
import org.pronsky.exceptions.UnableToUpdateException;

import java.sql.*;
import java.util.*;

/**
 * DAO implementation for the ProductCategory entity using JDBC.
 * This class provides methods for database operations related to product categories.
 */
@Log4j2
@RequiredArgsConstructor
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
    private static final String FIND_ALL_BY_PRODUCT_ID = "SELECT pc.name, pc.id, ct.name AS type_name " +
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
    private static final String COLUMN_TYPE_NAME = "type_name";
    private static final String COLUMN_NAME = "name";
    private final ConnectionUtil connectionUtil;

    /**
     * Retrieves a product category by its ID from the database.
     *
     * @param id The ID of the product category to retrieve.
     * @return The product category with the specified ID.
     * @throws UnableToFindException If an error occurs during the retrieval process.
     */
    @Override
    public ProductCategory getById(Long id) {
        log.debug("ProductCategoryDAOImpl.getById");
        ProductCategory productCategory = new ProductCategory();
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_CATEGORY_BY_ID)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            setParameters(productCategory, result);
            log.debug("fetched category : " + productCategory);
            return productCategory;
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    /**
     * Retrieves all product categories from the database.
     *
     * @return A list of all product categories.
     * @throws UnableToFindException If an error occurs during the retrieval process.
     */
    @Override
    public List<ProductCategory> getAll() {
        log.debug("ProductCategoryDAOImpl.getAll");
        List<ProductCategory> categories = new ArrayList<>();
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_CATEGORIES)) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                long id = result.getLong(COLUMN_ID);
                ProductCategory category = getById(id);
                categories.add(category);
            }
            log.debug("Fetched categories : " + categories);
            return categories;
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    /**
     * Creates a new product category record in the database.
     *
     * @param productCategory The product category object to create.
     * @return The created product category object with the generated ID.
     * @throws UnableToCreateException If an error occurs during the creation process.
     */
    @Override
    public ProductCategory create(ProductCategory productCategory) {
        log.debug("ProductCategoryDAOImpl.create");
        try (Connection connection = connectionUtil.getConnection();
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

    /**
     * Updates an existing product category record in the database.
     *
     * @param productCategory The product category object to update.
     * @return The updated product category object.
     * @throws UnableToUpdateException If an error occurs during the update process.
     */
    @Override
    public ProductCategory update(ProductCategory productCategory) {
        log.debug("ProductCategoryDAOImpl.update");
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_CATEGORY)) {
            prepareStatementForUpdate(productCategory, statement);
            statement.executeUpdate();
            return getById(productCategory.getId());
        } catch (SQLException e) {
            throw new UnableToUpdateException(e);
        }
    }

    /**
     * Deletes a product category record from the database by its ID.
     *
     * @param id The ID of the product category to delete.
     * @return True if the deletion was successful, false otherwise.
     * @throws UnableToDeleteException If an error occurs during the deletion process.
     */
    @Override
    public boolean deleteById(Long id) {
        log.debug("ProductCategoryDAOImpl.deleteById");
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_CATEGORY)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new UnableToDeleteException(e);
        }
    }

    /**
     * Retrieves all product categories associated with a specific product ID.
     *
     * @param productId The ID of the product.
     * @return A list of product categories associated with the product.
     * @throws UnableToFindException If an error occurs during the retrieval process.
     */
    @Override
    public Set<ProductCategory> getAllByProductId(Long productId) {
        log.debug("ProductCategoryDAOImpl.getAllByProductId");
        try (Connection connection = connectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_PRODUCT_ID)) {
            Map<Long, ProductCategory> productCategoryMap = new HashMap<>();
            statement.setLong(1, productId);
            ResultSet resultSet = statement.executeQuery();
            Set<CategoryType> types = new HashSet<>();
            while (resultSet.next()) {
                Long categoryId = resultSet.getLong(COLUMN_ID);
                String name = resultSet.getString(COLUMN_NAME);
                String typeName = resultSet.getString(COLUMN_TYPE_NAME);
                CategoryType categoryType = CategoryType.valueOf(typeName);
                ProductCategory productCategory = new ProductCategory();
                productCategory.setId(categoryId);
                productCategory.setName(name);
                types.add(categoryType);
                productCategory.setTypes(types);
                productCategoryMap.put(categoryId, productCategory);
            }
            log.debug("Fetched categories : " + productCategoryMap);
            return new HashSet<>(productCategoryMap.values());
        } catch (SQLException e) {
            throw new UnableToFindException(e);
        }
    }

    private void createCategoryToTypeRelation(ProductCategory category, Connection connection) throws SQLException {
        for (CategoryType type : category.getTypes()) {
            try (PreparedStatement statement = connection.prepareStatement(CREATE_CATEGORY_TO_TYPE_RELATION)) {
                statement.setLong(1, category.getId());
                statement.setLong(2, type.ordinal());
            } catch (SQLException e) {
                throw new UnableToCreateException("Unable to create relations", e);
            }
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
