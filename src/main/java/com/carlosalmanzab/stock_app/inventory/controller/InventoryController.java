package com.carlosalmanzab.stock_app.inventory.controller;

import com.carlosalmanzab.stock_app.inventory.repository.projection.StockMovementView;
import com.carlosalmanzab.stock_app.inventory.repository.projection.StockMovementWithProductUuidView;
import com.carlosalmanzab.stock_app.inventory.repository.projection.StockView;
import com.carlosalmanzab.stock_app.inventory.service.InventoryService;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/products")
public class InventoryController {
  private final InventoryService inventoryService;

  @GetMapping("/movements")
  public ResponseEntity<Page<StockMovementWithProductUuidView>> getStockMovements(
      @ParameterObject Pageable pageable) {
    return ResponseEntity.ok(inventoryService.getMovements(pageable));
  }

  @GetMapping("/{productUuid}/movements")
  public ResponseEntity<Page<StockMovementView>> getStockMovementsByProductUuid(
      @ParameterObject Pageable pageable, @PathVariable UUID productUuid) {
    return ResponseEntity.ok(inventoryService.getMovementsByProductUuid(pageable, productUuid));
  }

  @GetMapping("/{productUuid}/stock")
  public ResponseEntity<StockView> getStockByProductUuid(@PathVariable UUID productUuid) {
    return ResponseEntity.ok(inventoryService.getStockViewByProductUuid(productUuid));
  }

  @PostMapping("/movements")
  public ResponseEntity<StockMovementView> createMovement(@RequestBody StockMovementCreateDto dto) {
    StockMovementView view = inventoryService.movementTypeHandler(dto);
    URI uri = URI.create("/api/v1/products/movements/" + view.uuid());
    return ResponseEntity.created(uri).body(view);
  }
}
