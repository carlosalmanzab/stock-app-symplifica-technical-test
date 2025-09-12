package com.carlosalmanzab.stock_app.product.controller;

import com.carlosalmanzab.stock_app.product.repository.projection.ProductView;
import com.carlosalmanzab.stock_app.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Endpoints for managing products")
public class ProductController {
  private final ProductService service;

    @GetMapping("/{uuid}")
    @Operation(summary = "Get product by UUID", description = "Retrieve a product by its unique UUID")
    public ResponseEntity<ProductView> getByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getByUuid(uuid));
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Returns a paginated list of products")
    public ResponseEntity<Page<ProductView>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @PostMapping
    @Operation(summary = "Create a product", description = "Adds a new product to the system")
    public ResponseEntity<ProductView> save(@Valid @RequestBody ProductCreateDto createDto) {
        ProductView productView = service.create(createDto);
        URI uri = URI.create("/api/v1/products/" + productView.uuid());
        return ResponseEntity.created(uri).body(productView);
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update a product", description = "Updates an existing product by UUID")
    public ResponseEntity<ProductView> update(
            @PathVariable UUID uuid,
            @Valid @RequestBody ProductUpdateDto dto) {
        return ResponseEntity.ok(service.update(uuid, dto));
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a product", description = "Deletes a product by UUID")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        service.delete(uuid);
        return ResponseEntity.noContent().build();
    }

}
