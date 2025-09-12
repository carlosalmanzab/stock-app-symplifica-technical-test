package com.carlosalmanzab.stock_app.product.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductUpdateDto(
    BigDecimal price, String name, String desc, LocalDateTime expiration) {}
