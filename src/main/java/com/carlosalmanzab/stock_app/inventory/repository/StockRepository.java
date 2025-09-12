package com.carlosalmanzab.stock_app.inventory.repository;

import com.carlosalmanzab.stock_app.inventory.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    <T> Optional<T> findByProductUuid(UUID productUuid, Class<T> type);
}
