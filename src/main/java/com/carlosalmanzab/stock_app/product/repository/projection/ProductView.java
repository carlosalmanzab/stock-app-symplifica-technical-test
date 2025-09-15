package com.carlosalmanzab.stock_app.product.repository.projection;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductView(
    @Schema(
            description = "Unique identifier (UUID) of the product",
            example = "550e8400-e29b-41d4-a716-446655440000")
        UUID uuid,
    @Schema(description = "Price of the product", example = "99.99") BigDecimal price,
    @Schema(description = "Name of the product", example = "Laptop Dell XPS 15") String name,
    @Schema(
            description = "Short description of the product",
            example = "High-performance laptop with 16GB RAM")
        String desc,
    @Schema(description = "Expiration date (if applicable)", example = "2026-01-01T00:00:00")
        LocalDateTime expiration,
    @Schema(description = "Creation timestamp", example = "2025-09-09T14:00:00")
        LocalDateTime createdAt) {}
