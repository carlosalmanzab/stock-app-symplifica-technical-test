package com.carlosalmanzab.stock_app.inventory.controller;

import com.carlosalmanzab.stock_app.inventory.model.MovementType;

import java.util.UUID;

public record StockMovementCreateDto(
        UUID productUuid,
        int quantity,
        MovementType type
) {}
