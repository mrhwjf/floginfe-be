package com.floginfe_be.backend.service.impl;

import com.floginfe_be.backend.dto.request.ProductFilterRequest;
import com.floginfe_be.backend.dto.request.ProductRequest;
import com.floginfe_be.backend.dto.response.PagedResponse;
import com.floginfe_be.backend.dto.response.ProductDto;
import com.floginfe_be.backend.entity.Product;
import com.floginfe_be.backend.exception.ResourceAlreadyExistsException;
import com.floginfe_be.backend.exception.ResourceNotFoundException;
import com.floginfe_be.backend.mapper.ProductMapper;
import com.floginfe_be.backend.repository.ProductRepository;
import com.floginfe_be.backend.service.ProductService;
import com.floginfe_be.backend.spec.ProductSpec;
import com.floginfe_be.backend.util.PagedResponseMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    private final ProductMapper mapper;

    public ProductDto createProduct(ProductRequest request) {
        validateProduct(request);
        Product product = mapper.toEntity(request);
        if (repository.existsByNameIgnoreCase(product.getName())) {
            throw new ResourceAlreadyExistsException("Product name already exists!");
        }
        return mapper.toDto(repository.save(product));
    }

    public ProductDto updateProduct(Long id, ProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found!"));
        if (request.getName() != null && repository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new ResourceAlreadyExistsException("Product name already exists!");
        }
        mapper.updateEntityFromDto(request, product);
        return mapper.toDto(repository.save(product));
    }

    public void deleteProduct(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product with id " + id + " not found!");
        }
        repository.deleteById(id);
    }

    public ProductDto getProductById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found!"));
        return mapper.toDto(product);
    }

    public PagedResponse<ProductDto> getAllProducts(ProductFilterRequest request, Pageable pageable) {
        Specification<Product> spec = ProductSpec.byFilter(request);
        Page<Product> pg = repository.findAll(spec, pageable);
        return PagedResponseMapper.fromPage(pg, mapper::toDto);
    }

    private void validateProduct(ProductRequest request) {

        // -------- Name ----------
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        int nameLen = request.getName().length();
        if (nameLen < 3 || nameLen > 100) {
            throw new IllegalArgumentException("Tên sản phẩm phải từ 3 đến 100 ký tự");
        }

        // -------- Price ----------
        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải là số dương");
        }
        if (request.getPrice() > 999_999_999) {
            throw new IllegalArgumentException("Giá sản phẩm phải <= 999.999.999");
        }

        // -------- Quantity ----------
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng phải là số không âm");
        }
        if (request.getQuantity() > 99_999) {
            throw new IllegalArgumentException("Số lượng phải <= 99999");
        }

        // -------- Category (enum) ----------
        if (request.getCategory() == null) {
            throw new IllegalArgumentException("Danh mục phải nằm trong danh sách đã cho");
        }

        // -------- Description ----------
        if (request.getDescription() != null
                && request.getDescription().length() > 500) {
            throw new IllegalArgumentException("Mô tả sản phẩm không được vượt quá 500 ký tự");
        }
    }

}
