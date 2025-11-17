package com.floginfe_be.backend.service;

import com.floginfe_be.backend.service.impl.ProductService;

import com.floginfe_be.backend.entity.Product;
import com.floginfe_be.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test Validation Negative (Negative Test từ docx)
    @ParameterizedTest
    @CsvSource({
        "'', 1000.0, 5, 1, Name cannot be blank",
        "ab, 1000.0, 5, 1, Name must be 3-100 characters",
        "ValidName, -100.0, 5, 1, Price must be positive",
        "ValidName, 1000.0, -1, 1, Quantity cannot be negative",
        "ValidName, 1000.0, 5, , Category ID is required"
    })
    @DisplayName("PS-TC002 to TC005: Create/Update - Invalid Data - Throws Exception")
    void createProduct_InvalidData_ThrowsException(String name, double price, int quantity, Long categoryId, String expectedMsg) {
        Product invalid = new Product(null, name, price, quantity, categoryId);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.createProduct(invalid));
        assertEquals(expectedMsg, ex.getMessage());
    }

    // Test Create Success (Critical Priority)
    @Test
    @DisplayName("PS-TC001: Create Product - Valid Data - Success")
    void createProduct_ValidData_ReturnsSavedProduct() {
        Product input = new Product(null, "Laptop Dell", 1500.0, 10, 1L);
        Product saved = new Product(1L, "Laptop Dell", 1500.0, 10, 1L);
        when(repository.save(any(Product.class))).thenReturn(saved);
        Product result = service.createProduct(input);
        assertNotNull(result.getId());
        assertEquals("Laptop Dell", result.getName());
        verify(repository, times(1)).save(input);
    }

    @Test
    @DisplayName("PS-TC001b: Create Product - Duplicate name - Throws IllegalArgumentException")
    void createProduct_DuplicateName_Throws() {
        Product input = new Product(null, "Laptop Dell", 1500.0, 10, 1L);
        when(repository.existsByNameIgnoreCase("Laptop Dell")).thenReturn(true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.createProduct(input));
        assertEquals("Product name already exists", ex.getMessage());
        verify(repository, never()).save(any());
    }

    // Test Get By ID (Read chi tiết từ docx)
    @Test
    @DisplayName("PS-TC005: Get Product By ID - Exists - Success")
    void getProductById_Exists_ReturnsProduct() {
        Product product = new Product(1L, "Laptop", 1000.0, 10, 1L);
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        Product result = service.getProductById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("PS-TC006: Get Product By ID - Not Exists - Throws Exception")
    void getProductById_NotExists_ThrowsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getProductById(99L));
        assertEquals("Product not found", ex.getMessage());
    }

    // Test Get All with Pagination and Search (Read Filter từ docx)
    @Test
    @DisplayName("PS-TC007: Get All Products - Pagination - Returns Page")
    void getAllProducts_Pagination_ReturnsPage() {
        List<Product> products = Arrays.asList(new Product(1L, "Laptop", 1000.0, 10, 1L));
        Page<Product> page = new PageImpl<>(products);
        when(repository.findByNameContainingIgnoreCase("", PageRequest.of(0, 10))).thenReturn(page);
        Page<Product> result = service.getAllProducts(0, 10, null);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("PS-TC008: Search Products By Name - Returns List")
    void searchProducts_ByName_ReturnsList() {
        List<Product> products = Arrays.asList(new Product(1L, "Dell Laptop", 1000.0, 10, 1L));
        Page<Product> page = new PageImpl<>(products);
        when(repository.findByNameContainingIgnoreCase("Dell", PageRequest.of(0, 10))).thenReturn(page);
        Page<Product> result = service.getAllProducts(0, 10, "Dell");
        assertEquals("Dell Laptop", result.getContent().get(0).getName());
    }

    // Test Update (Test case cho Update từ docx)
    @Test
    @DisplayName("PS-TC009: Update Product - Valid - Success")
    void updateProduct_Valid_ReturnsUpdated() {
        Product existing = new Product(1L, "Old Name", 500.0, 5, 1L);
        Product updateInput = new Product(null, "New Name", 1000.0, 10, 1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Product.class))).thenReturn(new Product(1L, "New Name", 1000.0, 10, 1L));
        Product result = service.updateProduct(1L, updateInput);
        assertEquals("New Name", result.getName());
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("PS-TC009b: Update Product - Duplicate name (other id) - Throws IllegalArgumentException")
    void updateProduct_DuplicateName_Throws() {
        Product existing = new Product(1L, "Old Name", 500.0, 5, 1L);
        Product updateInput = new Product(null, "Existing Name", 1000.0, 10, 1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByNameIgnoreCaseAndIdNot("Existing Name", 1L)).thenReturn(true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.updateProduct(1L, updateInput));
        assertEquals("Product name already exists", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("PS-TC010: Update Product - Not Exists - Throws Exception")
    void updateProduct_NotExists_ThrowsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.updateProduct(99L, new Product()));
        assertEquals("Product not found", ex.getMessage());
    }

    // Test Delete (Test case cho Delete từ docx)
    @Test
    @DisplayName("PS-TC011: Delete Product - Exists - Success")
    void deleteProduct_Exists_CallsDelete() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);
        service.deleteProduct(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("PS-TC012: Delete Product - Not Exists - Throws RuntimeException")
    void deleteProduct_NotExists_ThrowsException() {
        when(repository.existsById(99L)).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteProduct(99L));
        assertEquals("Product not found", ex.getMessage());
        verify(repository, never()).deleteById(99L);
    }

    // Additional Scenarios (ít nhất 10 từ docx, priority: Critical for CRUD, High for validation, Medium for edges)
    @Test
    @DisplayName("Extra: Create with Max Name Length (100 chars) - Success (Medium Priority)")
    void createProduct_MaxNameLength_Success() {
        String maxName = "A".repeat(100);
        Product input = new Product(null, maxName, 1000.0, 10, 1L);
        when(repository.save(any())).thenReturn(input);
        Product result = service.createProduct(input);
        assertEquals(100, result.getName().length());
    }

    @Test
    @DisplayName("Extra: Create with Min Price (0.01) - Success (High Priority)")
    void createProduct_MinPrice_Success() {
        Product input = new Product(null, "Valid", 0.01, 0, 1L);
        when(repository.save(any())).thenReturn(input);
        assertDoesNotThrow(() -> service.createProduct(input));
    }

    @Test
    @DisplayName("Extra: Create with Price > 999999999 - Throws IllegalArgumentException")
    void createProduct_PriceTooHigh_Throws() {
        Product input = new Product(null, "Valid", 1_000_000_000D, 0, 1L);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.createProduct(input));
        assertEquals("Price must be <= 999999999", ex.getMessage());
        verify(repository, never()).save(any());
    }

    // ... Thêm nếu cần hơn 10, nhưng đủ rồi cho coverage
}
