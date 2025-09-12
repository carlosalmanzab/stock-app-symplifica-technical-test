package com.carlosalmanzab.stock_app.product.service;

import com.carlosalmanzab.stock_app.inventory.service.InventoryService;
import com.carlosalmanzab.stock_app.product.controller.ProductCreateDto;
import com.carlosalmanzab.stock_app.product.controller.ProductUpdateDto;
import com.carlosalmanzab.stock_app.product.model.Product;
import com.carlosalmanzab.stock_app.product.repository.ProductMapper;
import com.carlosalmanzab.stock_app.product.repository.ProductRepository;
import com.carlosalmanzab.stock_app.product.repository.projection.ProductView;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductService {
  private final ProductRepository repository;
  private final InventoryService stockService;
  private final ProductMapper mapper;

  public ProductView create(ProductCreateDto createDto) {
    Product saved = repository.save(mapper.toProduct(createDto));
    stockService.initStock(saved);
    return mapper.toView(saved);
  }

  @Transactional(readOnly = true)
  public ProductView getByUuid(UUID uuid) {
    return repository
        .findByUuid(uuid, ProductView.class)
        .orElseThrow(() -> new RuntimeException("Product Not found"));
  }

  @Transactional(readOnly = true)
  public Page<ProductView> getAll(Pageable pageable) {
    return repository.findAllBy(pageable, ProductView.class);
  }

  public ProductView update(UUID uuid, ProductUpdateDto updateDto) {
    Product ProductToUpdate =
        repository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("Product Not found"));

    mapper.UpdateFromDto(updateDto, ProductToUpdate);

    return mapper.toView(repository.save(ProductToUpdate));
  }

  public void delete(UUID uuid) {
    Product product =
        repository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("Product Not found"));

    repository.delete(product);
  }
}
