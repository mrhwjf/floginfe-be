package com.floginfe_be.backend.dto.response;

import com.floginfe_be.backend.constants.Categories;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ProductDto {
	private Long id;
	private String name;
	private double price;
	private int quantity;
	private Categories category;
}
