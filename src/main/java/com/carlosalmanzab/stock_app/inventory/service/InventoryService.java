package com.carlosalmanzab.stock_app.inventory.service;

import com.carlosalmanzab.stock_app.inventory.controller.StockMovementCreateDto;
import com.carlosalmanzab.stock_app.inventory.model.MovementType;
import com.carlosalmanzab.stock_app.inventory.model.Stock;
import com.carlosalmanzab.stock_app.inventory.model.StockMovement;
import com.carlosalmanzab.stock_app.inventory.repository.StockMapper;
import com.carlosalmanzab.stock_app.inventory.repository.StockMovementMapper;
import com.carlosalmanzab.stock_app.inventory.repository.StockMovementRepository;
import com.carlosalmanzab.stock_app.inventory.repository.StockRepository;
import com.carlosalmanzab.stock_app.inventory.repository.projection.StockMovementView;
import com.carlosalmanzab.stock_app.inventory.repository.projection.StockMovementWithProductUuidView;
import com.carlosalmanzab.stock_app.inventory.repository.projection.StockView;
import com.carlosalmanzab.stock_app.product.model.Product;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class InventoryService {
  private final StockRepository stockRepository;
  private final StockMovementRepository movementRepository;
  private final StockMapper stockMapper;
  private final StockMovementMapper movementMapper;

  @Transactional(readOnly = true)
  private Stock getStockByProductUuid(UUID prodUuid) {
    return stockRepository
        .findByProductUuid(prodUuid, Stock.class)
        .orElseThrow(() -> new RuntimeException("Stock not found for product " + prodUuid));
  }

  public StockView getStockViewByProductUuid(UUID prodUuid) {
    return stockMapper.toView(getStockByProductUuid(prodUuid));
  }

  public StockMovementView movementTypeHandler(StockMovementCreateDto movementCreateDto) {
    return switch (movementCreateDto.type()) {
      case IN -> increaseStock(movementCreateDto.productUuid(), movementCreateDto.quantity());
      case OUT -> decreaseStock(movementCreateDto.productUuid(), movementCreateDto.quantity());
    };
  }

  private StockMovementView decreaseStock(UUID prodUuid, int decreaseQuantity) {
    Stock stock = getStockByProductUuid(prodUuid);

    int totalStock = stock.getCurrentStock() - decreaseQuantity;
    if (decreaseQuantity <= 0) {
      throw new IllegalArgumentException("La cantidad a disminuir debe ser mayor a 0");
    }
    if (stock.getCurrentStock() < decreaseQuantity || totalStock < 0) {
      throw new IllegalStateException(
          "No hay stock suficiente. Stock actual: "
              + stock.getCurrentStock()
              + ", solicitado: "
              + decreaseQuantity);
    }
    stock.setCurrentStock(totalStock);
    Stock savedStock = stockRepository.save(stock);

    StockMovement stockMovement =
        movementRepository.save(
            StockMovement.builder()
                .product(savedStock.getProduct())
                .type(MovementType.OUT)
                .build());

    return movementMapper.toView(stockMovement);
  }

  private StockMovementView increaseStock(UUID prodUuid, int increaseQuantity) {
    Stock stock = getStockByProductUuid(prodUuid);

    if (increaseQuantity <= 0) {
      throw new IllegalArgumentException("La cantidad a aumentar debe ser mayor a 0");
    }

    stock.setCurrentStock(stock.getCurrentStock() + increaseQuantity);

    Stock savedStock = stockRepository.save(stock);

    StockMovement savedMovement =
        movementRepository.save(
            StockMovement.builder().product(savedStock.getProduct()).type(MovementType.IN).build());
    return movementMapper.toView(savedMovement);
  }

  public void initStock(Product product) {
    stockRepository.save(Stock.builder().product(product).currentStock(0).build());
  }

  @Transactional(readOnly = true)
  public Page<StockMovementWithProductUuidView> getMovements(Pageable pageable) {
    return movementRepository.findAllStockMovementWithProductUuid(pageable);
  }

  @Transactional(readOnly = true)
  public Page<StockMovementView> getMovementsByProductUuid(Pageable pageable, UUID prodUuid) {
    return movementRepository.findByProductUuid(prodUuid, pageable, StockMovementView.class);
  }
}
