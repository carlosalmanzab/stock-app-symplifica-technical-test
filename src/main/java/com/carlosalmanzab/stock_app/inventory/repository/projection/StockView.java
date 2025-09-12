package com.carlosalmanzab.stock_app.inventory.repository.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockView(
        UUID uuid,
        int currentStock,
        LocalDateTime createdAt
) {}
