package org.pronsky.service.impl;

import lombok.RequiredArgsConstructor;
import org.pronsky.data.repository.OrderDetailRepository;
import org.pronsky.service.OrderDetailsService;
import org.pronsky.service.dto.OrderDetailsDTO;
import org.pronsky.service.mapper.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of the {@link OrderDetailsService} interface.
 * This class provides methods for managing order details.
 */
@RequiredArgsConstructor
public class OrderDetailsServiceImpl implements OrderDetailsService {
    private final Mapper mapper;
    private final OrderDetailRepository repository;

    /**
     * Retrieves order details by its ID.
     *
     * @param id The ID of the order details to retrieve.
     * @return The order details with the specified ID, or null if not found.
     */
    @Override
    public OrderDetailsDTO getById(Long id) {
        return mapper.toDto(repository.findById(id));
    }

    /**
     * Retrieves all order details.
     *
     * @return A list of all order details.
     */
    @Override
    public List<OrderDetailsDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Saves new order details or updates an existing one.
     * This method also calculates the total amount of the order details
     * based on the products, their prices, and quantities.
     *
     * @param orderDetailsDTO The order details DTO to save or update.
     * @return The saved or updated order details DTO.
     */
    @Override
    public OrderDetailsDTO save(OrderDetailsDTO orderDetailsDTO) {
        BigDecimal totalAmount = orderDetailsDTO.getProducts().stream()
                .map(productDTO -> productDTO.getPrice()
                        .multiply(BigDecimal.valueOf(productDTO.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        orderDetailsDTO.setTotalAmount(totalAmount);
        return mapper.toDto(repository.save(mapper.toEntity(orderDetailsDTO)));
    }

    /**
     * Deletes order details by its ID.
     *
     * @param id The ID of the order details to delete.
     */
    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
