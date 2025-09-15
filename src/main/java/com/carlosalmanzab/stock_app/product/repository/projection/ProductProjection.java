package com.carlosalmanzab.stock_app.product.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ProductProjection {
    String getUuid();
    BigDecimal getPrice();
    String getName();
    String getDesc();
    LocalDateTime getExpiration();
    LocalDateTime getCreatedAt();
}
