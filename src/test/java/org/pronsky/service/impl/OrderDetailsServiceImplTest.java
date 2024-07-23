package org.pronsky.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pronsky.data.entities.OrderDetails;
import org.pronsky.data.repository.OrderDetailRepository;
import org.pronsky.service.dto.OrderDetailsDTO;
import org.pronsky.service.dto.ProductDTO;
import org.pronsky.service.mapper.Mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderDetailsServiceImplTest {

    @Mock
    private Mapper mapper;

    @Mock
    private OrderDetailRepository repository;

    @InjectMocks
    private OrderDetailsServiceImpl service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetById() {
        Long id = 1L;
        OrderDetails orderDetails = new OrderDetails();
        OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
        when(repository.findById(id)).thenReturn(orderDetails);
        when(mapper.toDto(orderDetails)).thenReturn(orderDetailsDTO);
        OrderDetailsDTO result = service.getById(id);
        assertEquals(orderDetailsDTO, result);
        verify(repository, times(1)).findById(id);
        verify(mapper, times(1)).toDto(orderDetails);
    }

    @Test
    void testGetAll() {
        List<OrderDetails> orderDetailsList = new ArrayList<>();
        orderDetailsList.add(new OrderDetails());
        orderDetailsList.add(new OrderDetails());
        List<OrderDetailsDTO> orderDetailsDTOList = new ArrayList<>();
        orderDetailsDTOList.add(new OrderDetailsDTO());
        orderDetailsDTOList.add(new OrderDetailsDTO());
        when(repository.findAll()).thenReturn(orderDetailsList);
        when(mapper.toDto(any(OrderDetails.class))).thenReturn(new OrderDetailsDTO());
        List<OrderDetailsDTO> result = service.getAll();
        assertEquals(orderDetailsDTOList.size(), result.size());
        verify(repository, times(1)).findAll();
        verify(mapper, times(orderDetailsList.size())).toDto(any(OrderDetails.class));
    }

    @Test
    void testSave() {
        OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
        List<ProductDTO> products = new ArrayList<>();
        ProductDTO product1 = new ProductDTO();
        product1.setPrice(new BigDecimal("10.00"));
        product1.setQuantity(2);
        products.add(product1);
        orderDetailsDTO.setProducts(products);
        OrderDetails orderDetails = new OrderDetails();
        when(mapper.toEntity(orderDetailsDTO)).thenReturn(orderDetails);
        when(repository.save(orderDetails)).thenReturn(orderDetails);
        when(mapper.toDto(orderDetails)).thenReturn(orderDetailsDTO);
        OrderDetailsDTO result = service.save(orderDetailsDTO);
        assertEquals(new BigDecimal("20.00"), result.getTotalAmount());
        verify(mapper, times(1)).toEntity(orderDetailsDTO);
        verify(repository, times(1)).save(orderDetails);
        verify(mapper, times(1)).toDto(orderDetails);
    }

    @Test
    void testDelete() {
        Long id = 1L;
        service.delete(id);
        verify(repository, times(1)).delete(id);
    }
}
