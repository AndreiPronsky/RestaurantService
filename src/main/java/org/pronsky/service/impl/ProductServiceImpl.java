package org.pronsky.service.impl;

import lombok.RequiredArgsConstructor;
import org.pronsky.data.repository.ProductRepository;
import org.pronsky.service.ProductService;
import org.pronsky.service.dto.ProductDTO;
import org.pronsky.service.mapper.Mapper;

import java.util.List;

/**
 * Implementation of the {@link ProductService} interface.
 * This class provides methods for managing products.
 */
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final Mapper mapper;
    private final ProductRepository repository;

    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product to retrieve.
     * @return The product with the specified ID, or null if not found.
     */
    @Override
    public ProductDTO getById(Long id) {
        return mapper.toDto(repository.findById(id));
    }

    /**
     * Retrieves all products.
     *
     * @return A list of all products.
     */
    @Override
    public List<ProductDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Saves a new product or updates an existing one.
     *
     * @param productDTO The product DTO to save or update.
     * @return The saved or updated product DTO.
     */
    @Override
    public ProductDTO save(ProductDTO productDTO) {
        return mapper.toDto(repository.save(mapper.toEntity(productDTO)));
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to delete.
     */
    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
