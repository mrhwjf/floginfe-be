package com.floginfe_be.backend.service;

import org.springframework.data.domain.Pageable;

import com.floginfe_be.backend.dto.request.ProductFilterRequest;
import com.floginfe_be.backend.dto.request.ProductRequest;
import com.floginfe_be.backend.dto.response.PagedResponse;
import com.floginfe_be.backend.dto.response.ProductDto;

public interface ProductService {
	ProductDto createProduct(ProductRequest request);

	ProductDto updateProduct(Long id, ProductRequest request);

	void deleteProduct(Long id);

	ProductDto getProductById(Long id);

	PagedResponse<ProductDto> getAllProducts(ProductFilterRequest request, Pageable pageable);
}
