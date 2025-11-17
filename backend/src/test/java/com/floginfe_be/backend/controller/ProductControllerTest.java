package com.floginfe_be.backend.controller;

import com.floginfe_be.backend.entity.Product;
import com.floginfe_be.backend.service.impl.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Laptop Dell", 1500.0, 10, 1L);
    }

    @Test
    @DisplayName("POST /api/products - Create Success - 201")
    void createProduct_Success() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop Dell"));
    }

    @Test
    @DisplayName("POST /api/products - Invalid Data - 400")
    void createProduct_Invalid_BadRequest() throws Exception {
        Product invalid = new Product(null, "", -100.0, -1, null);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/products - Price too high - 400")
    void createProduct_PriceTooHigh_BadRequest() throws Exception {
        Product tooHigh = new Product(null, "Laptop Dell", 1_000_000_000D, 10, 1L);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tooHigh)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details[0].field").value("price"));
    }

    @Test
    @DisplayName("POST /api/products - Duplicate name - 400")
    void createProduct_DuplicateName_BadRequest() throws Exception {
        when(productService.createProduct(any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product name already exists"));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Product name already exists"));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Success - 200")
    void getProductById_Success() throws Exception {
        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop Dell"));
    }

    @Test
    @DisplayName("GET /api/products?page=0&size=10&search=Dell - Success")
    void getAllProducts_WithSearch_Success() throws Exception {
        Page<Product> page = new PageImpl<>(Collections.singletonList(product));
        when(productService.getAllProducts(0, 10, "Dell")).thenReturn(page);

        mockMvc.perform(get("/api/products?page=0&size=10&search=Dell"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Laptop Dell"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Success - 200")
    void updateProduct_Success() throws Exception {
        Product updated = new Product(1L, "MacBook", 2000.0, 5, 1L);
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updated);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("MacBook"));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Success - 204")
    void deleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(1L);
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Not Found - 404")
    void deleteProduct_NotFound() throws Exception {
        doThrow(new RuntimeException("Product not found")).when(productService).deleteProduct(99L);

        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Product not found"));
    }

    // Interaction verification
    @Test
    @DisplayName("Verify service.createProduct called exactly once")
    void createProduct_VerifyInteraction() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(product);
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)));
        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Verify service.updateProduct called with correct ID")
    void updateProduct_VerifyInteraction() throws Exception {
        Product updated = new Product(1L, "MacBook", 2000.0, 5, 1L);
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updated);
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)));
        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("Verify service.deleteProduct called")
    void deleteProduct_VerifyInteraction() throws Exception {
        doNothing().when(productService).deleteProduct(1L);
        mockMvc.perform(delete("/api/products/1"));
        verify(productService, times(1)).deleteProduct(1L);
    }
}
