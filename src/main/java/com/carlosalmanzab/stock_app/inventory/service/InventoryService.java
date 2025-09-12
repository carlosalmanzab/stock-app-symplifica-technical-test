package com.carlosalmanzab.stock_app.inventory.service;

import com.carlosalmanzab.stock_app.common.exception.NotFoundException;
import com.carlosalmanzab.stock_app.common.exception.ValidationException;
import com.carlosalmanzab.stock_app.inventory.controller.StockMovementCreateDto;
import com.carlosalmanzab.stock_app.inventory.model.MovementType;
import com.carlosalmanzab.stock_app.inventory.model.Stock;
import com.carlosalmanzab.stock_app.inventory.model.StockMovement;
import com.carlosalmanzab.stock_app.inventory.repository.StockMapper;
import com.carlosalmanzab.stock_app.inventory.repository.StockMovementMapper;
import com.carlosalmanzab.stock_app.inventory.repository.StockMovementRepository;
import com.carlosalmanzab.stock_app.inventory.repository.StockRepository;
import com.carlosalmanzab.stock_app.inventory.repository.projection.StockMovementView;
import com.carlosalmanzab.stock_app.inventory.repository.projection.StockMovementWithStockUuidView;
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
        .orElseThrow(() -> new NotFoundException("Stock not found for product " + prodUuid));
  }

  public StockView getStockViewByProductUuid(UUID prodUuid) {
    return stockMapper.toView(getStockByProductUuid(prodUuid));
  }

  public StockMovementView movementTypeHandler(StockMovementCreateDto movementCreateDto) {
    return switch (movementCreateDto.type()) {
      case IN -> updateStock(movementCreateDto.productUuid(), movementCreateDto.quantity(),MovementType.IN);
      case OUT -> updateStock(movementCreateDto.productUuid(), movementCreateDto.quantity(),MovementType.OUT);
    };
  }

    private StockMovementView updateStock(UUID prodUuid, int quantity, MovementType type) {
        Stock stock = getStockByProductUuid(prodUuid);

        int newStock = type == MovementType.IN
                ? stock.getCurrentStock() + quantity
                : stock.getCurrentStock() - quantity;

        if (newStock < 0) {
            throw new ValidationException("There es insufficient stock. Current stock "
                    + stock.getCurrentStock()
                    + ", Request: "
                    + quantity);
        }

        stock.setCurrentStock(newStock);
        Stock savedStock = stockRepository.save(stock);

        StockMovement movement = StockMovement.builder()
                .stock(savedStock)
                .quantity(quantity)
                .type(type)
                .build();

        return movementMapper.toView(movementRepository.save(movement));
    }

  public void initStock(Product product) {
    stockRepository.save(Stock.builder().product(product).currentStock(0).build());
  }

  @Transactional(readOnly = true)
  public Page<StockMovementWithStockUuidView> getMovements(Pageable pageable) {
    return movementRepository.findAllStockMovementWithStockUuid(pageable);
  }

  @Transactional(readOnly = true)
  public Page<StockMovementView> getMovementsByProductUuid(Pageable pageable, UUID prodUuid) {
      Stock stock = getStockByProductUuid(prodUuid);
    return movementRepository.findByStock_Uuid(stock.getUuid(), pageable, StockMovementView.class);
  }
}
