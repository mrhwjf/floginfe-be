package com.floginfe_be.backend.service.impl;

import com.floginfe_be.backend.entity.Product;
import com.floginfe_be.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public Product createProduct(Product product) {
        validateProduct(product);
        if (repository.existsByNameIgnoreCase(product.getName())) {
            throw new IllegalArgumentException("Product name already exists");
        }
        return repository.save(product);
    }

    public Product updateProduct(Long id, Product product) {
        Product existing = repository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getName() != null && repository.existsByNameIgnoreCaseAndIdNot(product.getName(), id)) {
            throw new IllegalArgumentException("Product name already exists");
        }
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setQuantity(product.getQuantity());
        existing.setCategoryId(product.getCategoryId());
        validateProduct(existing);
        return repository.save(existing);
    }

    public void deleteProduct(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        repository.deleteById(id);
    }

    public Product getProductById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Page<Product> getAllProducts(int page, int size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return repository.findByNameContainingIgnoreCase(search == null ? "" : search, pageRequest);
    }

    private void validateProduct(Product p) {
        if (p.getName() == null || p.getName().trim().isEmpty()) throw new IllegalArgumentException("Name cannot be blank");
        if (p.getName().length() < 3 || p.getName().length() > 100) throw new IllegalArgumentException("Name must be 3-100 characters");
        if (p.getPrice() <= 0) throw new IllegalArgumentException("Price must be positive");
        if (p.getPrice() > 999_999_999D) throw new IllegalArgumentException("Price must be <= 999999999");
        if (p.getQuantity() < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        if (p.getCategoryId() == null) throw new IllegalArgumentException("Category ID is required");
    }
}
