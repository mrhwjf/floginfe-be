package com.floginfe_be.backend.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.floginfe_be.backend.constants.Categories;
import com.floginfe_be.backend.dto.request.ProductFilterRequest;
import com.floginfe_be.backend.dto.request.ProductRequest;
import com.floginfe_be.backend.dto.response.PagedResponse;
import com.floginfe_be.backend.dto.response.ProductDto;
import com.floginfe_be.backend.entity.Product;
import com.floginfe_be.backend.exception.ResourceAlreadyExistsException;
import com.floginfe_be.backend.exception.ResourceNotFoundException;
import com.floginfe_be.backend.mapper.ProductMapper;
import com.floginfe_be.backend.repository.ProductRepository;
import com.floginfe_be.backend.service.impl.ProductServiceImpl;

@DisplayName("Product Service Unit Testing")
class ProductServiceUnitTest {

	@Mock
	private ProductRepository repository;

	@Mock
	private ProductMapper mapper;

	@InjectMocks
	private ProductServiceImpl service;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	// -----------------------------------------------------------
	// PS-TC002…PS-TC006 – Validation (Negative tests from docx)
	// -----------------------------------------------------------
	// @ParameterizedTest
	// @CsvSource({
	// "'', 1000.0, 5, LAPTOP, Tên sản phẩm không được để trống",
	// "ab, 1000.0, 5, LAPTOP, Tên sản phẩm phải từ 3 đến 100 ký tự",
	// "ValidName, -100.0, 5, LAPTOP, Giá sản phẩm phải là số dương",
	// "ValidName, 1000.0, -1, LAPTOP, Số lượng phải là số không âm",
	// "ValidName, 1000.0, 5, , Danh mục là bắt buộc"
	// })
	// @DisplayName("PS-TC002–TC006: Create - Invalid Request Data - Throws
	// IllegalArgumentException")
	// void createProduct_InvalidRequest_Throws(String name, double price, int
	// quantity, Categories category,
	// String expectedMsg) {

	// ProductRequest req = new ProductRequest(name, price, quantity, category,
	// "description");

	// IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()
	// -> service.createProduct(req));

	// assertEquals(expectedMsg, ex.getMessage());
	// }

	// -----------------------------------------------------------
	// PS-TC001 – Create Success
	// -----------------------------------------------------------
	@Test
	@DisplayName("PS-TC001: Create Product - Valid Data - Success")
	void createProduct_Valid_ReturnsDto() {

		ProductRequest req = new ProductRequest("Laptop Dell", 1500.0, 10, Categories.LAPTOP, "description");

		Product entity = new Product(null, "Laptop Dell", 1500.0, 10, Categories.LAPTOP, "description");
		Product saved = new Product(1L, "Laptop Dell", 1500.0, 10, Categories.LAPTOP, "description");
		ProductDto dto = new ProductDto(1L, "Laptop Dell", 1500.0, 10, Categories.LAPTOP, "description");
		when(mapper.toEntity(req)).thenReturn(entity);
		when(repository.existsByNameIgnoreCase("Laptop Dell")).thenReturn(false);
		when(repository.save(entity)).thenReturn(saved);
		when(mapper.toDto(saved)).thenReturn(dto);

		ProductDto result = service.createProduct(req);

		assertEquals(1L, result.getId());
		verify(repository).save(entity);
	}

	@Test
	@DisplayName("PS-TC001b: Create Product - Duplicate Name - Throws ResourceAlreadyExistsException")
	void createProduct_DuplicateName_Throws() {

		ProductRequest req = new ProductRequest("Laptop Dell", 1500.0, 10, Categories.LAPTOP, "description");
		Product entity = new Product(null, "Laptop Dell", 1500.0, 10, Categories.LAPTOP, "description");

		when(mapper.toEntity(req)).thenReturn(entity);
		when(repository.existsByNameIgnoreCase("Laptop Dell")).thenReturn(true);

		assertThrows(ResourceAlreadyExistsException.class, () -> service.createProduct(req));
		verify(repository, never()).save(any());
	}

	// -----------------------------------------------------------
	// PS-TC005…TC006 – Get by ID
	// -----------------------------------------------------------
	@Test
	@DisplayName("PS-TC005: Get Product By ID - Exists - Returns DTO")
	void getProductById_Exists_ReturnsDto() {

		Product product = new Product(1L, "Laptop", 1000.0, 3, Categories.LAPTOP, "description");
		ProductDto dto = new ProductDto(1L, "Laptop", 1000.0, 3, Categories.LAPTOP, "description");

		when(repository.findById(1L)).thenReturn(Optional.of(product));
		when(mapper.toDto(product)).thenReturn(dto);

		ProductDto result = service.getProductById(1L);

		assertEquals(1L, result.getId());
	}

	@Test
	@DisplayName("PS-TC006: Get Product By ID - Not Exists - Throws ResourceNotFoundException")
	void getProductById_NotFound_Throws() {

		when(repository.findById(99L)).thenReturn(Optional.empty());

		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.getProductById(99L));

		assertEquals("Product with id 99 not found!", ex.getMessage());
	}

	// -----------------------------------------------------------
	// PS-TC007…TC008 – Pagination + Filtering
	// -----------------------------------------------------------
	@Test
	@DisplayName("PS-TC007: Get All Products - Pagination - Returns Page of DTOs")
	void getAllProducts_ReturnsPagedResponse() {

		ProductFilterRequest filter = new ProductFilterRequest();
		Pageable pageable = PageRequest.of(0, 10);

		Product p = new Product(1L, "Laptop", 1000.0, 10, Categories.LAPTOP, "description");
		ProductDto dto = new ProductDto(1L, "Laptop", 1000.0, 10, Categories.LAPTOP, "description");

		Page<Product> page = new PageImpl<>(List.of(p));

		when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
		when(mapper.toDto(p)).thenReturn(dto);

		PagedResponse<ProductDto> result = service.getAllProducts(filter, pageable);

		assertEquals(1, result.getTotalElements());
		assertEquals("Laptop", result.getItems().get(0).getName());
	}

	@Test
	@DisplayName("PS-TC008: Filter By Name - Returns Matched Products")
	void filterByName_ReturnsMatched() {

		ProductFilterRequest filter = new ProductFilterRequest();
		filter.setSearch("lat");

		Pageable pageable = PageRequest.of(0, 10);

		Product p = new Product(1L, "Laptop", 1000.0, 10, Categories.LAPTOP, "description");
		ProductDto dto = new ProductDto(1L, "Laptop", 1000.0, 10, Categories.LAPTOP, "description");

		Page<Product> page = new PageImpl<>(List.of(p));

		when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
		when(mapper.toDto(p)).thenReturn(dto);

		PagedResponse<ProductDto> result = service.getAllProducts(filter, pageable);

		assertEquals("Laptop", result.getItems().get(0).getName());
	}

	// -----------------------------------------------------------
	// PS-TC009…TC010 – Update
	// -----------------------------------------------------------
	@Test
	@DisplayName("PS-TC009: Update Product - Valid Update - Returns Updated DTO")
	void updateProduct_Valid_ReturnsDto() {

		Long id = 1L;
		ProductRequest req = new ProductRequest("New Name", 2000.0, 5, Categories.LAPTOP, "description");

		Product existing = new Product(1L, "Old", 1000.0, 3, Categories.LAPTOP, "description");
		Product saved = new Product(1L, "New Name", 2000.0, 5, Categories.LAPTOP, "description");
		ProductDto dto = new ProductDto(1L, "New Name", 2000.0, 5, Categories.LAPTOP, "description");
		when(repository.findById(id)).thenReturn(Optional.of(existing));
		when(repository.existsByNameIgnoreCaseAndIdNot("New Name", id)).thenReturn(false);

		doAnswer(inv -> {
			ProductRequest r = inv.getArgument(0);
			Product p = inv.getArgument(1);
			p.setName(r.getName());
			p.setPrice(r.getPrice());
			p.setQuantity(r.getQuantity());
			p.setCategory(r.getCategory());
			return null;
		}).when(mapper).updateEntityFromDto(req, existing);

		when(repository.save(existing)).thenReturn(saved);
		when(mapper.toDto(saved)).thenReturn(dto);

		ProductDto result = service.updateProduct(id, req);

		assertEquals("New Name", result.getName());
	}

	@Test
	@DisplayName("PS-TC009b: Update Product - Duplicate Name - Throws ResourceAlreadyExistsException")
	void updateProduct_DuplicateName_Throws() {

		Long id = 1L;
		ProductRequest req = new ProductRequest("DupName", 1200.0, 3, Categories.LAPTOP, "description");
		Product existing = new Product(1L, "Old", 1000.0, 3, Categories.LAPTOP, "description");

		when(repository.findById(id)).thenReturn(Optional.of(existing));
		when(repository.existsByNameIgnoreCaseAndIdNot("DupName", id)).thenReturn(true);

		assertThrows(ResourceAlreadyExistsException.class, () -> service.updateProduct(id, req));
		verify(repository, never()).save(any());
	}

	@Test
	@DisplayName("PS-TC010: Update Product - Not Found - Throws ResourceNotFoundException")
	void updateProduct_NotFound_Throws() {

		when(repository.findById(99L)).thenReturn(Optional.empty());

		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
				() -> service.updateProduct(99L, new ProductRequest()));

		assertEquals("Product with id 99 not found!", ex.getMessage());
	}

	// -----------------------------------------------------------
	// PS-TC011…TC012 – Delete
	// -----------------------------------------------------------
	@Test
	@DisplayName("PS-TC011: Delete Product - Exists - Success")
	void deleteProduct_Exists_Deletes() {

		when(repository.existsById(1L)).thenReturn(true);
		service.deleteProduct(1L);
		verify(repository).deleteById(1L);
	}

	@Test
	@DisplayName("PS-TC012: Delete Product - Not Exists - Throws ResourceNotFoundException")
	void deleteProduct_NotExists_Throws() {

		when(repository.existsById(99L)).thenReturn(false);

		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.deleteProduct(99L));

		assertEquals("Product with id 99 not found!", ex.getMessage());
		verify(repository, never()).deleteById(any());
	}

	// -----------------------------------------------------------
	// EXTRA SCENARIOS (From docx)
	// -----------------------------------------------------------
	@Test
	@DisplayName("Extra: Create Product With Max Name (100 chars) – Success")
	void createProduct_MaxName_Success() {

		String name = "A".repeat(100);
		ProductRequest req = new ProductRequest(name, 100.0, 1, Categories.LAPTOP, "description");

		Product entity = new Product(null, name, 100.0, 1, Categories.LAPTOP, "description");

		when(mapper.toEntity(req)).thenReturn(entity);
		when(repository.existsByNameIgnoreCase(name)).thenReturn(false);
		when(repository.save(entity)).thenReturn(new Product(1L, name, 100.0, 1, Categories.LAPTOP, "description"));
		assertDoesNotThrow(() -> service.createProduct(req));
	}

	@Test
	@DisplayName("Extra: Create Product With Min Price (0.01) – Success")
	void createProduct_MinPrice_Success() {
		ProductRequest req = new ProductRequest("Valid", 0.01, 0, Categories.LAPTOP, "description");
		Product entity = new Product(null, "Valid", 0.01, 0, Categories.LAPTOP, "description");

		when(mapper.toEntity(req)).thenReturn(entity);
		when(repository.existsByNameIgnoreCase("Valid")).thenReturn(false);
		when(repository.save(entity)).thenReturn(new Product(1L, "Valid", 0.01, 0, Categories.LAPTOP, "description"));

		assertDoesNotThrow(() -> service.createProduct(req));
	}
}
