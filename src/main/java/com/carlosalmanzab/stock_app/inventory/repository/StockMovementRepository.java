package com.carlosalmanzab.stock_app.inventory.repository;

import com.carlosalmanzab.stock_app.inventory.model.StockMovement;
import com.carlosalmanzab.stock_app.inventory.repository.projection.StockMovementWithStockUuidView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    @Query("""
    SELECT new com.carlosalmanzab.stock_app.inventory.repository.projection.StockMovementWithStockUuidView(
      sm.uuid,
      sm.quantity,
      sm.createdAt,
      sm.stock.uuid
    )
    FROM StockMovement sm
    """)
    Page<StockMovementWithStockUuidView> findAllStockMovementWithStockUuid(Pageable pageable);

    <T> Page<T> findByStock_Uuid(UUID stockUuid, Pageable pageable, Class<T> type);
}
