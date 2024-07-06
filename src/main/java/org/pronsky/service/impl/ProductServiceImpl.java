package org.pronsky.service.impl;

import lombok.RequiredArgsConstructor;
import org.pronsky.data.repository.ProductRepository;
import org.pronsky.service.ProductService;
import org.pronsky.service.dto.ProductDTO;
import org.pronsky.service.mapper.Mapper;

import java.util.List;

@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final Mapper mapper;
    private final ProductRepository repository;

    @Override
    public ProductDTO getById(Long id) {
        return mapper.toDto(repository.findById(id));
    }

    @Override
    public List<ProductDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public ProductDTO save(ProductDTO productDTO) {
        return mapper.toDto(repository.save(mapper.toEntity(productDTO)));
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
