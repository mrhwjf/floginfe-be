package com.floginfe_be.backend.dto.request;

import com.floginfe_be.backend.constants.Categories;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ProductFilterRequest {

	private String search;

	private Categories category;

	@Positive
	private Double minPrice;

	@DecimalMin(value = "0.0", inclusive = true, message = "Giá tối đa phải ≥ 0")
	@DecimalMax(value = "999999999", inclusive = true, message = "Giá tối đa phải <= 999.999.999")
	private Double maxPrice;

	@PositiveOrZero(message = "Số lượng tối thiểu phải ≥ 0")
	private Integer minQuantity;

	@PositiveOrZero(message = "Số lượng tối đa phải ≥ 0")
	@DecimalMax(value = "99999", inclusive = true, message = "Số lượng tối đa phải <= 99999")
	private Integer maxQuantity;

	@AssertTrue(message = "Giá tối thiểu không thể lớn hơn giá tối đa")
	private boolean isPriceRangeValid() {
		return minPrice == null || maxPrice == null || minPrice <= maxPrice;
	}

	@AssertTrue(message = "Số lượng tối thiểu không thể lớn hơn số lượng tối đa")
	private boolean isQuantityRangeValid() {
		return minQuantity == null || maxQuantity == null || minQuantity <= maxQuantity;
	}
}
