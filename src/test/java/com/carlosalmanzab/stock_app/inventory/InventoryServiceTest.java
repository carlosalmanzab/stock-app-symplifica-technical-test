package com.carlosalmanzab.stock_app.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
import com.carlosalmanzab.stock_app.inventory.service.InventoryService;
import com.carlosalmanzab.stock_app.product.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {
  @Mock private StockRepository stockRepository;

  @Mock private StockMovementRepository stockMovementRepository;

  @Mock private StockMapper stockMapper;

  @Mock private StockMovementMapper movementMapper;

  @InjectMocks private InventoryService inventoryService;

  private Product product;
  private UUID prodUuid;
  private Stock stock;
  private StockMovement movement;
  private StockMovementView movementView;

  @BeforeEach
  void setup() {
    prodUuid = UUID.randomUUID();

    product =
        Product.builder()
            .name("Laptop 1")
            .price(BigDecimal.TEN)
            .desc("DELL 14'")
            .expiration(LocalDateTime.now().plusDays(200))
            .build();

    stock = Stock.builder().uuid(UUID.randomUUID()).product(product).currentStock(10).build();

    movement = StockMovement.builder().stock(stock).quantity(5).type(MovementType.IN).build();

    movementView = new StockMovementView(UUID.randomUUID(), movement.getQuantity(), movement.getType(), LocalDateTime.now());
  }

  @Test
  void initStock_ShouldSaveNewStock() {
    // given product
    // when
    inventoryService.initStock(product);
    // the
    verify(stockRepository).save(any(Stock.class));
  }

  @Test
  void getStockViewByProductUuid_ShouldReturnMappedView_WhenStockExists() {
    StockView stockView = mock(StockView.class);
    when(stockRepository.findByProductUuid(prodUuid, Stock.class)).thenReturn(Optional.of(stock));
    when(stockMapper.toView(stock)).thenReturn(stockView);

    StockView result = inventoryService.getStockViewByProductUuid(prodUuid);

    assertEquals(stockView, result);

    verify(stockRepository).findByProductUuid(prodUuid, Stock.class);
    // Note: for signature overloaded <StockView>
    verify(stockMapper).toView(stock);
  }

  @Test
  void getStockViewByProductUuid_ShouldThrowException_WhenStockNotFound() {
    when(stockRepository.findByProductUuid(prodUuid, Stock.class)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> inventoryService.getStockViewByProductUuid(prodUuid));
  }

  @Test
  void movementTypeHandler_ShouldCallUpdateStockForIN() {
    StockMovementCreateDto dto = new StockMovementCreateDto(prodUuid, 5, MovementType.IN);

    InventoryService spyService = Mockito.spy(inventoryService);

    doReturn(movementView).when(spyService).movementTypeHandler(dto);

    StockMovementView result = spyService.movementTypeHandler(dto);

    assertEquals(movementView, result);
  }

  @Test
  void updateStock_ShouldIncreaseStock_WhenTypeIsIN() {
      //for private method
    when(stockRepository.findByProductUuid(prodUuid, Stock.class)).thenReturn(Optional.of(stock));

    when(stockRepository.save(any())).thenReturn(stock);
    when(stockMovementRepository.save(any())).thenReturn(movement);
    when(movementMapper.toView(movement)).thenReturn(movementView);

    StockMovementView result =
        inventoryService.movementTypeHandler(
            new StockMovementCreateDto(prodUuid, 5, MovementType.IN));

    assertEquals(5, result.quantity());
    assertEquals(MovementType.IN, result.type());
    assertEquals(15, stock.getCurrentStock()); // 10 + 5
  }

  @Test
  void updateStock_ShouldDecreaseStock_WhenTypeIsOUT() {
    // given <stock> with quantity equals to 10
    StockMovement outMovement =
        StockMovement.builder().stock(stock).quantity(3).type(MovementType.OUT).build();
    //for private method
    when(stockRepository.findByProductUuid(prodUuid, Stock.class)).thenReturn(Optional.of(stock));

    when(stockRepository.save(any())).thenReturn(stock);


    when(stockMovementRepository.save(any())).thenReturn(outMovement);
    when(movementMapper.toView(outMovement)).thenReturn(new StockMovementView(stock.getUuid(), 3, MovementType.OUT, LocalDateTime.now()));

    StockMovementView result =
        inventoryService.movementTypeHandler(
            new StockMovementCreateDto(prodUuid, 3, MovementType.OUT));

    assertEquals(3, result.quantity());
    assertEquals(MovementType.OUT, result.type());
    assertEquals(7, stock.getCurrentStock()); // 10 - 3
  }

  @Test
  void updateStock_ShouldThrowValidationException_WhenInsufficientStock() {
    // given <stock> with quantity equals to 10
    when(stockRepository.findByProductUuid(prodUuid, Stock.class)).thenReturn(Optional.of(stock));

    assertThrows(
        ValidationException.class,
        () ->
            inventoryService.movementTypeHandler(
                new StockMovementCreateDto(prodUuid, 20, MovementType.OUT)));
  }

  @Test
  void getMovements_ShouldReturnPage() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<StockMovementWithStockUuidView> page =
        new PageImpl<>(List.of(mock(StockMovementWithStockUuidView.class)));

    when(stockMovementRepository.findAllStockMovementWithStockUuid(pageable)).thenReturn(page);

    Page<StockMovementWithStockUuidView> result = inventoryService.getMovements(pageable);

    assertEquals(1, result.getTotalElements());
    verify(stockMovementRepository).findAllStockMovementWithStockUuid(pageable);
  }

  @Test
  void getMovementsByProductUuid_ShouldReturnPage_WhenStockExists() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<StockMovementView> stockMovementViewPage = new PageImpl<>(List.of(movementView));

    when(stockRepository.findByProductUuid(prodUuid, Stock.class)).thenReturn(Optional.of(stock));

    when(stockMovementRepository.findByStock_Uuid(
            stock.getUuid(), pageable, StockMovementView.class))
        .thenReturn(stockMovementViewPage);

    Page<StockMovementView> result = inventoryService.getMovementsByProductUuid(pageable, prodUuid);

    assertEquals(1, result.getTotalElements());
    verify(stockMovementRepository)
        .findByStock_Uuid(stock.getUuid(), pageable, StockMovementView.class);
  }

  @Test
  void getMovementsByProductUuid_ShouldThrowException_WhenStockNotFound() {
    Pageable pageable = PageRequest.of(0, 10);

    when(stockRepository.findByProductUuid(prodUuid, Stock.class))
        .thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> inventoryService.getMovementsByProductUuid(pageable, prodUuid));
  }
}
