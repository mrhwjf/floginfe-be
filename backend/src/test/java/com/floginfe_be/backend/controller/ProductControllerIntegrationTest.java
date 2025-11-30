package com.floginfe_be.backend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.floginfe_be.backend.constants.Categories;
import com.floginfe_be.backend.dto.request.ProductRequest;
import com.floginfe_be.backend.dto.response.PagedResponse;
import com.floginfe_be.backend.dto.response.ProductDto;
import com.floginfe_be.backend.service.ProductService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.*;

import java.util.List;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Product API Integration Test")
class ProductControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ProductService productService;

	private ProductRequest buildRequest(String name, double price, int quantity, Categories category,
			String description) {
		return ProductRequest.builder()
				.name(name)
				.price(price)
				.quantity(quantity)
				.category(category)
				.description(description)
				.build();
	}

	@Test
	@DisplayName("POST /api/products - Create Product - 201")
	void createProduct_Success() throws Exception {
		ProductRequest request = buildRequest("ProdAlpha", 19999.0, 5, Categories.LAPTOP, "description");
		ProductDto dto = ProductDto.builder()
				.id(1L)
				.name("ProdAlpha")
				.price(19999.0)
				.quantity(5)
				.category(Categories.LAPTOP)
				.description("description")
				.build();

		when(productService.createProduct(any(ProductRequest.class))).thenReturn(dto);

		mockMvc.perform(post("/api/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.id").value(1))
				.andExpect(jsonPath("$.data.name").value("ProdAlpha"))
				.andExpect(jsonPath("$.data.category").value("LAPTOP"))
				.andExpect(jsonPath("$.data.description").value("description"));
	}

	@Test
	@DisplayName("GET /api/products - Get All Products - 200")
	void getAllProducts_Success() throws Exception {
		ProductDto p1 = ProductDto.builder().id(1L).name("ProdOne").category(Categories.DESKTOP)
				.description("description").price(1000.0).quantity(2).build();
		ProductDto p2 = ProductDto.builder().id(2L).name("ProdTwo").category(Categories.TABLET)
				.description("description").price(1500.0).quantity(3).build();

		PagedResponse<ProductDto> pagedResponse = PagedResponse.<ProductDto>builder()
				.items(List.of(p1, p2))
				.totalElements(2)
				.totalPages(1)
				.page(0)
				.size(2)
				.hasNext(false)
				.hasPrevious(false)
				.build();

		when(productService.getAllProducts(any(), any(Pageable.class))).thenReturn(pagedResponse);

		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.items.length()").value(2))
				.andExpect(jsonPath("$.data.totalElements").value(2))
				.andExpect(jsonPath("$.data.totalPages").value(1))
				.andExpect(jsonPath("$.data.items[0].name").value("ProdOne"))
				.andExpect(jsonPath("$.data.items[1].name").value("ProdTwo"));
	}

	@Test
	@DisplayName("GET /api/products/{id} - Get Product By ID - 200")
	void getProductById_Success() throws Exception {
		ProductDto dto = ProductDto.builder()
				.id(1L)
				.name("ProdGet")
				.price(500.0)
				.quantity(1)
				.category(Categories.SMARTPHONE)
				.description("description")
				.build();

		when(productService.getProductById(1L)).thenReturn(dto);

		mockMvc.perform(get("/api/products/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.id").value(1))
				.andExpect(jsonPath("$.data.name").value("ProdGet"))
				.andExpect(jsonPath("$.data.description").value("description"));
	}

	@Test
	@DisplayName("PUT /api/products/{id} - Update Product - 200")
	void updateProduct_Success() throws Exception {
		ProductRequest request = buildRequest("ProdUpdated", 999.0, 10, Categories.MONITOR, "description");
		ProductDto dto = ProductDto.builder()
				.id(1L)
				.name("ProdUpdated")
				.price(999.0)
				.quantity(10)
				.category(Categories.MONITOR)
				.description("description")
				.build();

		when(productService.updateProduct(eq(1L), any(ProductRequest.class))).thenReturn(dto);

		mockMvc.perform(put("/api/products/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.id").value(1))
				.andExpect(jsonPath("$.data.name").value("ProdUpdated"))
				.andExpect(jsonPath("$.data.price").value(999.0))
				.andExpect(jsonPath("$.data.quantity").value(10))
				.andExpect(jsonPath("$.data.description").value("description"));
	}

	@Test
	@DisplayName("DELETE /api/products/{id} - Delete Product - 204")
	void deleteProduct_Success() throws Exception {
		doNothing().when(productService).deleteProduct(1L);

		mockMvc.perform(delete("/api/products/1"))
				.andExpect(status().isNoContent());
	}
}