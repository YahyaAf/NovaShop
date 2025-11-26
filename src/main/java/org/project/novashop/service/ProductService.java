package org. project.novashop.service;

import org.project.novashop.dto.api.ApiResponse;
import org.project. novashop.dto.products.ProductRequestDto;
import org.project. novashop.dto.products. ProductResponseDto;
import org. project.novashop.exception. DuplicateResourceException;
import org.project.novashop.exception.ResourceNotFoundException;
import org.project.novashop. mapper.ProductMapper;
import org. project.novashop.model. Product;
import org.project. novashop.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain. Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation. Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ApiResponse<ProductResponseDto> create(ProductRequestDto requestDto) {
        if (productRepository.existsByNomAndDeletedFalse(requestDto.getNom())) {
            throw new DuplicateResourceException("Product", "nom", requestDto.getNom());
        }

        Product product = productMapper.toEntity(requestDto);
        Product savedProduct = productRepository.save(product);
        ProductResponseDto responseDto = productMapper. toResponseDto(savedProduct);

        return new ApiResponse<>("Produit créé avec succès", responseDto);
    }

    @Transactional
    public ApiResponse<ProductResponseDto> update(Long id, ProductRequestDto requestDto) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (productRepository. existsByNomAndDeletedFalseAndIdNot(requestDto.getNom(), id)) {
            throw new DuplicateResourceException("Product", "nom", requestDto.getNom());
        }

        productMapper.updateEntityFromDto(requestDto, product);
        Product updatedProduct = productRepository.save(product);
        ProductResponseDto responseDto = productMapper.toResponseDto(updatedProduct);

        return new ApiResponse<>("Produit mis à jour avec succès", responseDto);
    }

    @Transactional
    public ApiResponse<Void> softDelete(Long id) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setDeleted(true);
        productRepository.save(product);

        return new ApiResponse<>("Produit supprimé avec succès");
    }

    public ApiResponse<Page<ProductResponseDto>> findAllWithFilters(
            String nom,
            Double minPrice,
            Double maxPrice,
            Boolean inStock,
            Pageable pageable) {

        Page<Product> products = productRepository. findAllWithFilters(
                nom, minPrice, maxPrice, inStock, pageable
        );

        Page<ProductResponseDto> responsePage = products.map(productMapper::toResponseDto);

        return new ApiResponse<>("Liste des produits récupérée avec succès", responsePage);
    }

    public ApiResponse<ProductResponseDto> findById(Long id) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        ProductResponseDto responseDto = productMapper.toResponseDto(product);

        return new ApiResponse<>("Produit récupéré avec succès", responseDto);
    }
}