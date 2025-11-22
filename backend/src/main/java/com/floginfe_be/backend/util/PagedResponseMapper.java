package com.floginfe_be.backend.util;

import java.util.function.Function;

import org.springframework.data.domain.Page;

import com.floginfe_be.backend.dto.response.PagedResponse;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PagedResponseMapper {
	public static <E, D> PagedResponse<D> fromPage(Page<E> page, Function<E, D> mapper) {
		return PagedResponse.<D>builder()
				.items(page.getContent().stream().map(mapper).toList())
				.page(page.getNumber())
				.size(page.getSize())
				.totalElements(page.getTotalElements())
				.totalPages(page.getTotalPages())
				.hasNext(page.hasNext())
				.hasPrevious(page.hasPrevious())
				.build();
	}
}
