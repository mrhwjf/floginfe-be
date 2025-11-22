package com.floginfe_be.backend.dto.request;

import com.floginfe_be.backend.constants.Categories;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ProductRequest {
	@NotBlank(message = "Tên sản phẩm không được để trống")
	@Size(min = 3, max = 100, message = "Tên sản phẩm phải từ 3 đến 100 ký tự")
	private String name;

	@Positive(message = "Giá sản phẩm phải là số dương")
	@DecimalMax(value = "999999999", inclusive = true, message = "Giá sản phẩm phải <= 999.999.999")
	private Double price;

	@PositiveOrZero(message = "Số lượng phải là số không âm")
	@DecimalMax(value = "99999", inclusive = true, message = "Số lượng phải <= 99999")
	private Integer quantity;

	@Enumerated(EnumType.STRING)
	private Categories category;
}
