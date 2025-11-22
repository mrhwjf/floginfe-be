package com.floginfe_be.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.floginfe_be.backend.constants.Categories;
import com.floginfe_be.backend.dto.request.ProductRequest;
import com.floginfe_be.backend.entity.Product;
import com.floginfe_be.backend.repository.ProductRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Product API Integration Test")
public class ProductControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
	}

	private ProductRequest buildRequest(String name, double price, int quantity, Categories category) {
		return ProductRequest.builder()
				.name(name)
				.price(price)
				.quantity(quantity)
				.category(category)
				.build();
	}

	@Test
	@DisplayName("POST /api/products - Create Product - 201")
	void createProduct_Success() throws Exception {
		ProductRequest request = buildRequest("ProdAlpha", 19999.0, 5, Categories.LAPTOP);

		mockMvc.perform(post("/api/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.id").isNumber())
				.andExpect(jsonPath("$.data.name").value("ProdAlpha"))
				.andExpect(jsonPath("$.data.category").value("LAPTOP"));
	}

	@Test
	@DisplayName("GET /api/products - Get All Products - 200")
	void getAllProducts_Success() throws Exception {
		// Create products via repository for speed
		productRepository
				.save(Product.builder().name("ProdOne").price(1000.0).quantity(2).category(Categories.DESKTOP).build());
		productRepository
				.save(Product.builder().name("ProdTwo").price(1500.0).quantity(3).category(Categories.TABLET).build());

		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.items.length()").value(2))
				.andExpect(jsonPath("$.data.totalElements").value(2));
	}

	@Test
	@DisplayName("GET /api/products/{id} - Get Product By ID - 200")
	void getProductById_Success() throws Exception {
		Long id = productRepository.save(
				Product.builder().name("ProdGet").price(500.0).quantity(1).category(Categories.SMARTPHONE).build())
				.getId();

		mockMvc.perform(get("/api/products/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.id").value(id))
				.andExpect(jsonPath("$.data.name").value("ProdGet"));
	}

	@Test
	@DisplayName("PUT /api/products/{id} - Update Product - 200")
	void updateProduct_Success() throws Exception {
		Long id = productRepository
				.save(Product.builder().name("ProdOld").price(750.0).quantity(4).category(Categories.MONITOR).build())
				.getId();
		ProductRequest updateReq = buildRequest("ProdUpdated", 999.0, 10, Categories.MONITOR);

		mockMvc.perform(put("/api/products/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateReq)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.id").value(id))
				.andExpect(jsonPath("$.data.name").value("ProdUpdated"))
				.andExpect(jsonPath("$.data.price").value(999.0))
				.andExpect(jsonPath("$.data.quantity").value(10));
	}

	@Test
	@DisplayName("DELETE /api/products/{id} - Delete Product - 204")
	void deleteProduct_Success() throws Exception {
		Long id = productRepository
				.save(Product.builder().name("ProdDel").price(300.0).quantity(2).category(Categories.ACCESSORY).build())
				.getId();

		mockMvc.perform(delete("/api/products/" + id))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/products/" + id))
				.andExpect(status().isNotFound());
	}
}