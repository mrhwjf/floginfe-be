package com.floginfe_be.backend.controller;

import com.floginfe_be.backend.dto.request.ProductFilterRequest;
import com.floginfe_be.backend.dto.request.ProductRequest;
import com.floginfe_be.backend.dto.response.ApiResponse;
import com.floginfe_be.backend.dto.response.PagedResponse;
import com.floginfe_be.backend.dto.response.ProductDto;
import com.floginfe_be.backend.service.ProductService;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDto>> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo sản phẩm thành công", productService.createProduct(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Lấy sản phẩm với id " + id + " thành công",
                productService.getProductById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProductDto>>> getAll(
            @Valid @ModelAttribute @ParameterObject ProductFilterRequest request,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        PagedResponse<ProductDto> response = productService.getAllProducts(request, pageable);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách sản phẩm thành công", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> update(@PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật sản phẩm với id " + id + " thành công",
                productService.updateProduct(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
