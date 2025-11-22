package com.floginfe_be.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import com.floginfe_be.backend.dto.response.ProductDto;
import com.floginfe_be.backend.entity.Product;
import com.floginfe_be.backend.exception.ResourceAlreadyExistsException;
import com.floginfe_be.backend.exception.ResourceNotFoundException;
import com.floginfe_be.backend.mapper.ProductMapper;
import com.floginfe_be.backend.repository.ProductRepository;
import com.floginfe_be.backend.service.impl.ProductServiceImpl;

@DisplayName("Product Service - Mock Repository Tests")
class ProductServiceMockTest {

	@Mock
	private ProductRepository repository;

	@Mock
	private ProductMapper mapper;

	@InjectMocks
	private ProductServiceImpl service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// -----------------------------------------------------------
	// CREATE
	// -----------------------------------------------------------
	@Test
	@DisplayName("Create Product - Valid Data - Calls Repository Save")
	void createProduct_Valid_CallsRepository() {
		ProductRequest req = new ProductRequest("Laptop Dell", 1500.0, 10, Categories.LAPTOP);
		Product entity = new Product(null, "Laptop Dell", 1500.0, 10, Categories.LAPTOP);
		Product saved = new Product(1L, "Laptop Dell", 1500.0, 10, Categories.LAPTOP);

		when(mapper.toEntity(req)).thenReturn(entity);
		when(repository.existsByNameIgnoreCase("Laptop Dell")).thenReturn(false);
		when(repository.save(entity)).thenReturn(saved);
		when(mapper.toDto(saved)).thenReturn(new ProductDto(1L, "Laptop Dell", 1500.0, 10, Categories.LAPTOP));

		service.createProduct(req);

		// Verify repository interactions
		verify(repository).existsByNameIgnoreCase("Laptop Dell");
		verify(repository).save(entity);
	}

	@Test
	@DisplayName("Create Product - Duplicate Name - Throws Exception")
	void createProduct_DuplicateName_Throws() {
		ProductRequest req = new ProductRequest("Laptop Dell", 1500.0, 10, Categories.LAPTOP);
		Product entity = new Product(null, "Laptop Dell", 1500.0, 10, Categories.LAPTOP);

		when(mapper.toEntity(req)).thenReturn(entity);
		when(repository.existsByNameIgnoreCase("Laptop Dell")).thenReturn(true);

		assertThrows(ResourceAlreadyExistsException.class, () -> service.createProduct(req));

		// Verify repository save was never called
		verify(repository, never()).save(any());
	}

	// -----------------------------------------------------------
	// READ
	// -----------------------------------------------------------
	@Test
	@DisplayName("Get Product By ID - Exists - Calls Repository")
	void getProductById_Exists_CallsRepository() {
		Product product = new Product(1L, "Laptop", 1000.0, 3, Categories.LAPTOP);
		when(repository.findById(1L)).thenReturn(Optional.of(product));
		when(mapper.toDto(product)).thenReturn(new ProductDto(1L, "Laptop", 1000.0, 3, Categories.LAPTOP));

		service.getProductById(1L);

		verify(repository).findById(1L);
	}

	@Test
	@DisplayName("Get Product By ID - Not Exists - Throws Exception")
	void getProductById_NotFound_Throws() {
		when(repository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.getProductById(99L));

		verify(repository).findById(99L);
	}

	// -----------------------------------------------------------
	// UPDATE
	// -----------------------------------------------------------
	@Test
	@DisplayName("Update Product - Valid - Calls Repository Save")
	void updateProduct_Valid_CallsRepository() {
		Long id = 1L;
		ProductRequest req = new ProductRequest("New Name", 2000.0, 5, Categories.LAPTOP);
		Product existing = new Product(1L, "Old", 1000.0, 3, Categories.LAPTOP);

		when(repository.findById(id)).thenReturn(Optional.of(existing));
		when(repository.existsByNameIgnoreCaseAndIdNot("New Name", id)).thenReturn(false);
		when(repository.save(existing)).thenReturn(existing);
		doAnswer(inv -> {
			ProductRequest r = inv.getArgument(0);
			Product p = inv.getArgument(1);
			p.setName(r.getName());
			p.setPrice(r.getPrice());
			p.setQuantity(r.getQuantity());
			p.setCategory(r.getCategory());
			return null;
		}).when(mapper).updateEntityFromDto(req, existing);

		service.updateProduct(id, req);

		verify(repository).findById(id);
		verify(repository).existsByNameIgnoreCaseAndIdNot("New Name", id);
		verify(repository).save(existing);
	}

	// -----------------------------------------------------------
	// DELETE
	// -----------------------------------------------------------
	@Test
	@DisplayName("Delete Product - Exists - Calls Repository Delete")
	void deleteProduct_Exists_CallsRepository() {
		when(repository.existsById(1L)).thenReturn(true);

		service.deleteProduct(1L);

		verify(repository).existsById(1L);
		verify(repository).deleteById(1L);
	}

	@Test
	@DisplayName("Delete Product - Not Exists - Throws Exception")
	void deleteProduct_NotExists_Throws() {
		when(repository.existsById(99L)).thenReturn(false);

		assertThrows(ResourceNotFoundException.class, () -> service.deleteProduct(99L));

		verify(repository).existsById(99L);
		verify(repository, never()).deleteById(any());
	}

	// -----------------------------------------------------------
	// GET ALL (Pagination)
	// -----------------------------------------------------------
	@Test
	@DisplayName("Get All Products - Pagination - Calls Repository")
	void getAllProducts_ReturnsPagedResponse() {
		ProductFilterRequest filter = new ProductFilterRequest();
		Pageable pageable = PageRequest.of(0, 10);

		Product p = new Product(1L, "Laptop", 1000.0, 10, Categories.LAPTOP);
		Page<Product> page = new PageImpl<>(List.of(p));

		when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
		when(mapper.toDto(p)).thenReturn(new ProductDto(1L, "Laptop", 1000.0, 10, Categories.LAPTOP));

		service.getAllProducts(filter, pageable);

		verify(repository).findAll(any(Specification.class), eq(pageable));
	}
}
