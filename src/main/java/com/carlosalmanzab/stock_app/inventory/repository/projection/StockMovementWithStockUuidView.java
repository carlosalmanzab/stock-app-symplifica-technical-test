package com.carlosalmanzab.stock_app.inventory.repository.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockMovementWithStockUuidView(
        UUID uuid,
        int quantity,
        LocalDateTime createdAt,
        UUID stockUuid
) {}
