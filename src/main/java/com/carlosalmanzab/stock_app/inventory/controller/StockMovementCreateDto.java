package com.carlosalmanzab.stock_app.inventory.controller;

import com.carlosalmanzab.stock_app.inventory.model.MovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StockMovementCreateDto(
        @NotNull()
        UUID productUuid,

        @Min(value = 1)
        int quantity,

        @NotNull()
        MovementType type
) {}
