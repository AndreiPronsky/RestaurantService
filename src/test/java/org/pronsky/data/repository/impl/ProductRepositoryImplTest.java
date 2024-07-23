package org.pronsky.data.repository.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pronsky.data.dao.ProductCategoryDAO;
import org.pronsky.data.dao.ProductDAO;
import org.pronsky.data.entities.Product;
import org.pronsky.data.entities.ProductCategory;
import org.pronsky.exceptions.UnableToFindException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductRepositoryImplTest {

    private static final long EXISTING_ID = 1L;
    private static final long ANOTHER_EXISTING_ID = 2L;
    private static final long NOT_EXISTING_ID = 10000000L;
    private static Set<ProductCategory> categorySet;
    private static Set<ProductCategory> anotherCategorySet;
    private static List<Product> existingProducts;
    private static Product existingProduct;
    private static Product anotherExistingProduct;
    private static Product notExistingProduct;
    private static Product createdProduct;
    private static ProductCategory existingProductCategory;
    private static ProductCategory anotherExistingProductCategory;

    @Mock
    private ProductDAO productDAO;

    @Mock
    private ProductCategoryDAO categoryDAO;

    @InjectMocks
    private ProductRepositoryImpl productRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    static void setUpBeforeClass() {
        existingProductCategory = new ProductCategory();
        existingProductCategory.setId(EXISTING_ID);
        existingProductCategory.setName("Existing");
        Set<ProductCategory.CategoryType> types = new HashSet<>();
        types.add(ProductCategory.CategoryType.PERISHABLE);
        types.add(ProductCategory.CategoryType.FRIDGE_STORAGE);
        existingProductCategory.setTypes(types);

        anotherExistingProductCategory = new ProductCategory();
        anotherExistingProductCategory.setId(ANOTHER_EXISTING_ID);
        anotherExistingProductCategory.setName("Another existing");
        types = new HashSet<>();
        types.add(ProductCategory.CategoryType.LONG_TERM);
        types.add(ProductCategory.CategoryType.FRIDGE_STORAGE);
        anotherExistingProductCategory.setTypes(types);

        categorySet = new HashSet<>();
        categorySet.add(existingProductCategory);
        categorySet.add(anotherExistingProductCategory);

        anotherCategorySet = new HashSet<>();
        anotherCategorySet.add(anotherExistingProductCategory);
        anotherCategorySet.add(existingProductCategory);

        existingProduct = new Product();
        existingProduct.setId(EXISTING_ID);
        existingProduct.setName("Existing product");
        existingProduct.setPrice(BigDecimal.valueOf(10.1));
        existingProduct.setQuantity(5);
        existingProduct.setAvailable(true);
        existingProduct.setProductCategories(categorySet);

        anotherExistingProduct = new Product();
        anotherExistingProduct.setId(ANOTHER_EXISTING_ID);
        anotherExistingProduct.setName("Another existing");
        anotherExistingProduct.setPrice(BigDecimal.valueOf(15.2));
        anotherExistingProduct.setQuantity(8);
        anotherExistingProduct.setAvailable(true);
        anotherExistingProduct.setProductCategories(anotherCategorySet);

        existingProducts = new ArrayList();
        existingProducts.add(existingProduct);
        existingProducts.add(anotherExistingProduct);

        notExistingProduct = new Product();
        notExistingProduct.setName("Not existing");
        notExistingProduct.setPrice(BigDecimal.valueOf(15.2));
        notExistingProduct.setQuantity(8);
        notExistingProduct.setAvailable(true);
        notExistingProduct.setProductCategories(anotherCategorySet);

        createdProduct = new Product();
        createdProduct.setId(3L);
        createdProduct.setName("Not existing");
        createdProduct.setPrice(BigDecimal.valueOf(15.2));
        createdProduct.setQuantity(8);
        createdProduct.setAvailable(true);
        createdProduct.setProductCategories(anotherCategorySet);
    }

    @Test
    void testFindByIdPositive() {
        when(productDAO.getById(EXISTING_ID)).thenReturn(existingProduct);
        when(categoryDAO.getAllByProductId(EXISTING_ID)).thenReturn(categorySet);
        Product result = productRepository.findById(EXISTING_ID);
        assertNotNull(result);
        assertEquals(existingProduct.getId(), result.getId());
        assertEquals(existingProduct.getName(), result.getName());
        assertEquals(existingProduct.getPrice(), result.getPrice());
        assertEquals(existingProduct.getQuantity(), result.getQuantity());
        assertEquals(existingProduct.isAvailable(), result.isAvailable());
        assertEquals(existingProduct.getProductCategories(), result.getProductCategories());
        verify(productDAO, times(1)).getById(EXISTING_ID);
        verify(categoryDAO, times(1)).getAllByProductId(EXISTING_ID);
    }

    @Test
    void testFindByIdNegative() {
        when(productDAO.getById(NOT_EXISTING_ID)).thenThrow(UnableToFindException.class);
        assertThrows(UnableToFindException.class, () -> productRepository.findById(NOT_EXISTING_ID));
    }

    @Test
    void testFindAll() {
        when(productDAO.getAll()).thenReturn(existingProducts);
        when(categoryDAO.getAllByProductId(1L)).thenReturn(categorySet);
        when(categoryDAO.getAllByProductId(2L)).thenReturn(anotherCategorySet);
        List<Product> result = productRepository.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(categorySet, result.get(0).getProductCategories());
        assertEquals(anotherCategorySet, result.get(1).getProductCategories());
        verify(productDAO, times(1)).getAll();
        verify(categoryDAO, times(1)).getAllByProductId(1L);
        verify(categoryDAO, times(1)).getAllByProductId(2L);
    }

    @Test
    void testSave_Create() {
        when(productDAO.create(notExistingProduct)).thenReturn(createdProduct);
        Product result = productRepository.save(notExistingProduct);
        assertNotNull(result);
        assertEquals(createdProduct, result);
        verify(productDAO, times(1)).create(notExistingProduct);
        verify(productDAO, never()).update(any(Product.class));
    }

    @Test
    void testSave_Update() {
        when(productDAO.update(existingProduct)).thenReturn(existingProduct);
        Product result = productRepository.save(existingProduct);
        assertNotNull(result);
        assertEquals(existingProduct, result);
        verify(productDAO, times(1)).update(existingProduct);
        verify(productDAO, never()).create(any(Product.class));
    }

    @Test
    void testDelete() {
        productRepository.delete(EXISTING_ID);
        verify(productDAO, times(1)).deleteById(EXISTING_ID);
    }
}