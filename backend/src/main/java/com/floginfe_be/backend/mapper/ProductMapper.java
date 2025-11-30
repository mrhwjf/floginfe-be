package com.floginfe_be.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.floginfe_be.backend.dto.request.ProductRequest;
import com.floginfe_be.backend.dto.response.ProductDto;
import com.floginfe_be.backend.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
	@Mapping(target = "id", ignore = true)
	Product toEntity(ProductRequest request);

	ProductDto toDto(Product product);

	@Mapping(target = "id", ignore = true)
	void updateEntityFromDto(ProductRequest request, @MappingTarget Product product);
}
