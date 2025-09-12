package com.carlosalmanzab.stock_app.product.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Data transfer object for creating a new Product")
public record ProductCreateDto(

        @Schema(description = "Price of the product", example = "199.99")
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
        BigDecimal price,

        @Schema(description = "Name of the product", example = "Wireless Headphones")
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @Schema(description = "Description of the product", example = "Noise-cancelling over-ear headphones")
        @Size(max = 255, message = "Description must not exceed 255 characters")
        String desc,

        @Schema(description = "Expiration date (must be in the future)", example = "2026-01-01T00:00:00")
        @Future(message = "Expiration date must be in the future")
        LocalDateTime expiration
) {}