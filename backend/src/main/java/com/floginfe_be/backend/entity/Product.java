package com.floginfe_be.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 100, message = "Name must be 3-100 characters")
    private String name;

    @Positive(message = "Price must be positive")
    @DecimalMax(value = "999999999", inclusive = true, message = "Price must be <= 999999999")
    private double price;

    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    // Constructors if needed
    public Product() {}
    public Product(Long id, String name, double price, int quantity, Long categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.categoryId = categoryId;
    }
}
