package org.pronsky.data.repository.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pronsky.data.dao.OrderDetailsDAO;
import org.pronsky.data.dao.ProductCategoryDAO;
import org.pronsky.data.dao.ProductDAO;
import org.pronsky.data.entities.OrderDetails;
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

class OrderDetailRepositoryImplTest {

    private static final long EXISTING_ID = 1L;
    private static final long ANOTHER_EXISTING_ID = 2L;
    private static final long NOT_EXISTING_ID = 10000000L;
    private static ProductCategory existingProductCategory;
    private static ProductCategory anotherExistingProductCategory;
    private static Set<ProductCategory> categorySet;
    private static Set<ProductCategory> anotherCategoryList;
    private static Product existingProduct;
    private static Product anotherExistingProduct;
    private static List<Product> productList;
    private static List<Product> anotherProductList;
    private static OrderDetails orderDetails;
    private static OrderDetails anotherOrderDetails;
    private static OrderDetails orderDetailsForCreate;
    private static List<OrderDetails> orderDetailsList;

    @Mock
    private OrderDetailsDAO orderDetailsDAO;

    @Mock
    private ProductDAO productDAO;

    @Mock
    private ProductCategoryDAO productCategoryDAO;

    @InjectMocks
    private OrderDetailRepositoryImpl orderDetailRepository;

    @BeforeAll
    static void setUpBeforeClass() {
        existingProductCategory = new ProductCategory();
        existingProductCategory.setId(1L);
        existingProductCategory.setName("Existing");
        Set<ProductCategory.CategoryType> types = new HashSet<>();
        types.add(ProductCategory.CategoryType.PERISHABLE);
        types.add(ProductCategory.CategoryType.FRIDGE_STORAGE);
        existingProductCategory.setTypes(types);

        anotherExistingProductCategory = new ProductCategory();
        anotherExistingProductCategory.setId(2L);
        anotherExistingProductCategory.setName("Another existing");
        types = new HashSet<>();
        types.add(ProductCategory.CategoryType.LONG_TERM);
        types.add(ProductCategory.CategoryType.FRIDGE_STORAGE);
        anotherExistingProductCategory.setTypes(types);

        categorySet = new HashSet<>();
        categorySet.add(existingProductCategory);
        categorySet.add(anotherExistingProductCategory);

        anotherCategoryList = new HashSet<>();
        categorySet.add(anotherExistingProductCategory);
        categorySet.add(existingProductCategory);

        existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Test Product");
        existingProduct.setPrice(BigDecimal.valueOf(2L));
        existingProduct.setQuantity(1);
        existingProduct.setAvailable(true);
        existingProduct.setProductCategories(categorySet);

        anotherExistingProduct = new Product();
        anotherExistingProduct.setId(2L);
        anotherExistingProduct.setName("Another existing");
        anotherExistingProduct.setPrice(BigDecimal.valueOf(3L));
        anotherExistingProduct.setQuantity(2);
        anotherExistingProduct.setAvailable(true);
        anotherExistingProduct.setProductCategories(anotherCategoryList);

        productList = new ArrayList<>();
        productList.add(existingProduct);
        productList.add(anotherExistingProduct);

        anotherProductList = new ArrayList<>();
        anotherProductList.add(anotherExistingProduct);
        anotherProductList.add(existingProduct);

        orderDetailsForCreate = new OrderDetails();
        orderDetailsForCreate.setProducts(productList);
        orderDetailsForCreate.setOrderStatus(OrderDetails.OrderStatus.OPEN);
        orderDetailsForCreate.setTotalAmount(BigDecimal.valueOf(12L));

        orderDetails = new OrderDetails();
        orderDetails.setId(1L);
        orderDetails.setProducts(productList);
        orderDetails.setOrderStatus(OrderDetails.OrderStatus.OPEN);
        orderDetails.setTotalAmount(BigDecimal.valueOf(12L));

        anotherOrderDetails = new OrderDetails();
        anotherOrderDetails.setId(2L);
        anotherOrderDetails.setProducts(productList);
        anotherOrderDetails.setOrderStatus(OrderDetails.OrderStatus.COMPLETED);
        anotherOrderDetails.setTotalAmount(BigDecimal.valueOf(15L));

        orderDetailsList = new ArrayList<>();
        orderDetailsList.add(orderDetails);
        orderDetailsList.add(anotherOrderDetails);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByIdPositive() {
        when(orderDetailsDAO.getById(EXISTING_ID)).thenReturn(orderDetails);
        when(productDAO.getAllByOrderId(EXISTING_ID)).thenReturn(productList);
        when(productCategoryDAO.getAllByProductId(EXISTING_ID)).thenReturn(categorySet);
        OrderDetails result = orderDetailRepository.findById(EXISTING_ID);
        assertNotNull(result);
        assertEquals(EXISTING_ID, result.getId());
        assertEquals(2, result.getProducts().size());
        assertEquals(categorySet, result.getProducts().get(0).getProductCategories());
        verify(orderDetailsDAO, times(1)).getById(EXISTING_ID);
        verify(productDAO, times(1)).getAllByOrderId(EXISTING_ID);
        verify(productCategoryDAO, times(1)).getAllByProductId(EXISTING_ID);
    }

    @Test
    void testFindByIdNegative() {
        when(orderDetailsDAO.getById(NOT_EXISTING_ID)).thenThrow(UnableToFindException.class);
        assertThrows(UnableToFindException.class, () -> orderDetailRepository.findById(NOT_EXISTING_ID));
    }

    @Test
    void testFindAll() {
        when(orderDetailsDAO.getAll()).thenReturn(orderDetailsList);
        when(productDAO.getAllByOrderId(EXISTING_ID)).thenReturn(productList);
        when(productDAO.getAllByOrderId(ANOTHER_EXISTING_ID)).thenReturn(anotherProductList);
        when(productCategoryDAO.getAllByProductId(EXISTING_ID)).thenReturn(categorySet);
        when(productCategoryDAO.getAllByProductId(ANOTHER_EXISTING_ID)).thenReturn(anotherCategoryList);
        List<OrderDetails> result = orderDetailRepository.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(categorySet, result.get(0).getProducts().get(0).getProductCategories());
        assertEquals(anotherCategoryList, result.get(1).getProducts().get(0).getProductCategories());
        verify(orderDetailsDAO, times(1)).getAll();
        verify(productDAO, times(1)).getAllByOrderId(EXISTING_ID);
        verify(productDAO, times(1)).getAllByOrderId(ANOTHER_EXISTING_ID);
        verify(productCategoryDAO, times(2)).getAllByProductId(EXISTING_ID);
        verify(productCategoryDAO, times(2)).getAllByProductId(ANOTHER_EXISTING_ID);
    }

    @Test
    void testSave_Create() {
        when(orderDetailsDAO.create(orderDetailsForCreate)).thenReturn(orderDetails);
        OrderDetails result = orderDetailRepository.save(orderDetailsForCreate);
        assertNotNull(result);
        assertEquals(orderDetails, result);
        verify(orderDetailsDAO, times(1)).create(orderDetailsForCreate);
        verify(orderDetailsDAO, never()).update(any(OrderDetails.class));
    }

    @Test
    void testSave_Update() {
        when(orderDetailsDAO.update(orderDetails)).thenReturn(orderDetails);
        OrderDetails result = orderDetailRepository.save(orderDetails);
        assertNotNull(result);
        assertEquals(orderDetails, result);
        verify(orderDetailsDAO, times(1)).update(orderDetails);
        verify(orderDetailsDAO, never()).create(any(OrderDetails.class));
    }

    @Test
    void testDelete() {
        orderDetailRepository.delete(EXISTING_ID);
        verify(orderDetailsDAO, times(1)).deleteById(EXISTING_ID);
    }
}