package com.floginfe_be.backend.spec;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.floginfe_be.backend.dto.request.ProductFilterRequest;
import com.floginfe_be.backend.entity.Product;
import com.floginfe_be.backend.entity.Product_;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProductSpec {

    public static Specification<Product> byFilter(ProductFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.getSearch())) {
                predicates
                        .add(cb.like(cb.lower(root.get(Product_.name)), "%" + filter.getSearch().toLowerCase() + "%"));
            }

            if (filter.getCategory() != null) {
                predicates.add(cb.equal(root.get(Product_.category), filter.getCategory()));
            }
            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Product_.price), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Product_.price), filter.getMaxPrice()));
            }

            if (filter.getMinQuantity() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Product_.quantity), filter.getMinQuantity()));
            }
            if (filter.getMaxQuantity() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Product_.quantity), filter.getMaxQuantity()));
            }

            // Combine all predicates with AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
