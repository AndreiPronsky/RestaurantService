package org.pronsky.service.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pronsky.data.entities.Product;
import org.pronsky.data.entities.ProductCategory;
import org.pronsky.data.repository.ProductRepository;
import org.pronsky.service.dto.ProductCategoryDTO;
import org.pronsky.service.dto.ProductDTO;
import org.pronsky.service.mapper.Mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {
    private static final Long EXISTING_ID = 1L;
    private static final Long ANOTHER_EXISTING_ID = 2L;
    private static final Long CREATED_ID = 3L;
    private static ProductDTO existingProductDTO;
    private static ProductDTO anotherExistingProductDTO;
    private static ProductDTO notExistingProductDTO;
    private static ProductDTO createdProductDTO;
    private static ProductDTO productDTOForUpdate;
    private static ProductDTO updatedProductDTO;
    private static Product existingProduct;
    private static Product notExistingProduct;
    private static Product createdProduct;
    private static Product productForUpdate;
    private static Product updatedProduct;
    private static ProductCategoryDTO existingProductCategoryDTO;
    private static ProductCategory existingProductCategory;
    private static List<ProductDTO> productDTOList;
    private static List<Product> productList;
    private static Set<ProductCategory> categorySet;
    private static Set<ProductCategoryDTO> categoryDTOSet;

    @Mock
    private Mapper mapper;

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    static void setUpBeforeClass() {
        Set<ProductCategoryDTO.CategoryType> types = new HashSet<>();
        types.add(ProductCategoryDTO.CategoryType.PERISHABLE);
        types.add(ProductCategoryDTO.CategoryType.FRIDGE_STORAGE);

        existingProductCategoryDTO = new ProductCategoryDTO();
        existingProductCategoryDTO.setId(EXISTING_ID);
        existingProductCategoryDTO.setName("Category");
        existingProductCategoryDTO.setTypes(types);

        categoryDTOSet = new HashSet<>();
        categoryDTOSet.add(existingProductCategoryDTO);

        existingProductDTO = new ProductDTO();
        existingProductDTO.setId(EXISTING_ID);
        existingProductDTO.setName("Existing product");
        existingProductDTO.setPrice(BigDecimal.valueOf(10.1));
        existingProductDTO.setQuantity(5);
        existingProductDTO.setAvailable(true);
        existingProductDTO.setProductCategories(categoryDTOSet);

        anotherExistingProductDTO = new ProductDTO();
        anotherExistingProductDTO.setId(ANOTHER_EXISTING_ID);
        anotherExistingProductDTO.setName("Another existing");
        anotherExistingProductDTO.setPrice(BigDecimal.valueOf(15.2));
        anotherExistingProductDTO.setQuantity(8);
        anotherExistingProductDTO.setAvailable(true);
        anotherExistingProductDTO.setProductCategories(categoryDTOSet);

        productDTOList = new ArrayList();
        productDTOList.add(existingProductDTO);
        productDTOList.add(anotherExistingProductDTO);

        notExistingProductDTO = new ProductDTO();
        notExistingProductDTO.setName("Not existing");
        notExistingProductDTO.setPrice(BigDecimal.valueOf(15.2));
        notExistingProductDTO.setQuantity(8);
        notExistingProductDTO.setAvailable(true);
        notExistingProductDTO.setProductCategories(categoryDTOSet);

        Set<ProductCategory.CategoryType> categoryTypes = new HashSet<>();
        categoryTypes.add(ProductCategory.CategoryType.LONG_TERM);
        categoryTypes.add(ProductCategory.CategoryType.FRIDGE_STORAGE);

        existingProductCategory = new ProductCategory();
        existingProductCategory.setId(EXISTING_ID);
        existingProductCategory.setName("Category");
        existingProductCategory.setTypes(categoryTypes);

        categorySet = new HashSet<>();
        categorySet.add(existingProductCategory);

        existingProduct = new Product();
        existingProduct.setId(EXISTING_ID);
        existingProduct.setName("Existing product");
        existingProduct.setPrice(BigDecimal.valueOf(10.1));
        existingProduct.setQuantity(5);
        existingProduct.setAvailable(true);
        existingProduct.setProductCategories(categorySet);

        notExistingProduct = new Product();
        notExistingProduct.setName("Not existing");
        notExistingProduct.setPrice(BigDecimal.valueOf(15.2));
        notExistingProduct.setQuantity(8);
        notExistingProduct.setAvailable(true);
        notExistingProduct.setProductCategories(categorySet);

        createdProductDTO = new ProductDTO();
        createdProductDTO.setId(CREATED_ID);
        createdProductDTO.setName("Not existing");
        createdProductDTO.setPrice(BigDecimal.valueOf(15.2));
        createdProductDTO.setQuantity(8);
        createdProductDTO.setAvailable(true);
        createdProductDTO.setProductCategories(categoryDTOSet);

        productDTOForUpdate = new ProductDTO();
        productDTOForUpdate.setId(EXISTING_ID);
        productDTOForUpdate.setName("Updated product");
        productDTOForUpdate.setPrice(BigDecimal.valueOf(10.1));
        productDTOForUpdate.setQuantity(5);
        productDTOForUpdate.setAvailable(true);
        productDTOForUpdate.setProductCategories(categoryDTOSet);

        updatedProductDTO = new ProductDTO();
        updatedProductDTO.setId(EXISTING_ID);
        updatedProductDTO.setName("Updated product");
        updatedProductDTO.setPrice(BigDecimal.valueOf(10.1));
        updatedProductDTO.setQuantity(5);
        updatedProductDTO.setAvailable(true);
        updatedProductDTO.setProductCategories(categoryDTOSet);

        createdProduct = new Product();
        createdProduct.setId(3L);
        createdProduct.setName("Not existing");
        createdProduct.setPrice(BigDecimal.valueOf(15.2));
        createdProduct.setQuantity(8);
        createdProduct.setAvailable(true);
        createdProduct.setProductCategories(categorySet);

        productForUpdate = new Product();
        productForUpdate.setId(EXISTING_ID);
        productForUpdate.setName("Updated product");
        productForUpdate.setPrice(BigDecimal.valueOf(10.1));
        productForUpdate.setQuantity(5);
        productForUpdate.setAvailable(true);
        productForUpdate.setProductCategories(categorySet);

        updatedProduct = new Product();
        updatedProduct.setId(EXISTING_ID);
        updatedProduct.setName("Updated product");
        updatedProduct.setPrice(BigDecimal.valueOf(10.1));
        updatedProduct.setQuantity(5);
        updatedProduct.setAvailable(true);
        updatedProduct.setProductCategories(categorySet);


        productList = new ArrayList();
        productList.add(existingProduct);
    }

    @Test
    void testGetById() {
        Long id = EXISTING_ID;
        when(repository.findById(id)).thenReturn(existingProduct);
        when(mapper.toDto(existingProduct)).thenReturn(existingProductDTO);
        ProductDTO result = service.getById(id);
        assertEquals(existingProductDTO, result);
        verify(repository, times(1)).findById(id);
        verify(mapper, times(1)).toDto(existingProduct);
    }

    @Test
    void testGetAll() {
        when(repository.findAll()).thenReturn(productList);
        when(mapper.toDto(any(Product.class))).thenReturn(new ProductDTO());
        List<ProductDTO> result = service.getAll();
        assertEquals(productList.size(), result.size());
        verify(repository, times(1)).findAll();
        verify(mapper, times(productList.size())).toDto(any(Product.class));
    }

    @Test
    void testSaveCreate() {
        when(mapper.toEntity(notExistingProductDTO)).thenReturn(notExistingProduct);
        when(repository.save(notExistingProduct)).thenReturn(createdProduct);
        when(mapper.toDto(createdProduct)).thenReturn(createdProductDTO);
        ProductDTO result = service.save(notExistingProductDTO);
        assertEquals(createdProductDTO, result);
        verify(mapper, times(1)).toEntity(notExistingProductDTO);
        verify(repository, times(1)).save(notExistingProduct);
        verify(mapper, times(1)).toDto(createdProduct);
    }

    @Test
    void testSaveUpdate() {
        when(mapper.toEntity(productDTOForUpdate)).thenReturn(productForUpdate);
        when(repository.save(productForUpdate)).thenReturn(updatedProduct);
        when(mapper.toDto(updatedProduct)).thenReturn(updatedProductDTO);
        ProductDTO result = service.save(productDTOForUpdate);
        assertEquals(updatedProductDTO, result);
        verify(mapper, times(1)).toEntity(productDTOForUpdate);
        verify(repository, times(1)).save(productForUpdate);
        verify(mapper, times(1)).toDto(updatedProduct);
    }

    @Test
    void testDelete() {
        Long id = 1L;
        service.delete(id);
        verify(repository, times(1)).delete(id);
    }
}
