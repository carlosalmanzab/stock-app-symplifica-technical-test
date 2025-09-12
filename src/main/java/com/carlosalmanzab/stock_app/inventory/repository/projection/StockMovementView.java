package com.carlosalmanzab.stock_app.inventory.repository.projection;

import com.carlosalmanzab.stock_app.inventory.model.MovementType;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockMovementView(
        UUID uuid,
        int quantity,
        MovementType type,
        LocalDateTime createdAt
) {}
