package com.carlosalmanzab.stock_app.inventory.repository;

import com.carlosalmanzab.stock_app.inventory.model.StockMovement;
import com.carlosalmanzab.stock_app.inventory.repository.projection.StockMovementWithProductUuidView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    @Query("SELECT com.carlosalmanzab.stock_app.inventory.repository.projection.StockMovementWithProductUuidView(sm.uuid,sm.quantity,sm.createdAt,sm.product.uuid) FROM StockMovement as sm")
    Page<StockMovementWithProductUuidView> findAllStockMovementWithProductUuid(Pageable pageable);
    <T> Page<T> findByProductUuid(UUID productUuid, Pageable pageable, Class<T> type);
}
