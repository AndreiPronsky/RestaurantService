package org.pronsky.service.impl;

import lombok.RequiredArgsConstructor;
import org.pronsky.data.repository.OrderDetailRepository;
import org.pronsky.service.OrderDetailsService;
import org.pronsky.service.dto.OrderDetailsDTO;
import org.pronsky.service.mapper.Mapper;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class OrderDetailsServiceImpl implements OrderDetailsService {
    private final Mapper mapper;
    private final OrderDetailRepository repository;

    @Override
    public OrderDetailsDTO getById(Long id) {
        return mapper.toDto(repository.findById(id));
    }

    @Override
    public List<OrderDetailsDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public OrderDetailsDTO save(OrderDetailsDTO orderDetailsDTO) {
        BigDecimal totalAmount = orderDetailsDTO.getProducts().stream()
                .map(productDTO -> productDTO.getPrice()
                        .multiply(BigDecimal.valueOf(productDTO.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        orderDetailsDTO.setTotalAmount(totalAmount);
        return mapper.toDto(repository.save(mapper.toEntity(orderDetailsDTO)));
    }

    @Override
    public void delete(OrderDetailsDTO orderDetailsDTO) {
        repository.delete(mapper.toEntity(orderDetailsDTO));
    }
}
