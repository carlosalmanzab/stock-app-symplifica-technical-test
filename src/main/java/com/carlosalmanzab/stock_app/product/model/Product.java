package com.carlosalmanzab.stock_app.product.model;

import com.carlosalmanzab.stock_app.inventory.model.Stock;
import com.carlosalmanzab.stock_app.inventory.model.StockMovement;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(
    name = "product",
    indexes = {
      @Index(name = "idx_products_name", columnList = "name"),
      @Index(name = "idx_products_expiration", columnList = "expiration")
    })
@SQLRestriction("active = true")
@SQLDelete(sql = "UPDATE product SET active = false WHERE id = ?")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(nullable = false, unique = true, updatable = false)
  private UUID uuid;

  @NotBlank
  @Size(max = 100)
  private String name;

  @Column(name = "description")
  private String desc;

  private BigDecimal price;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  private LocalDateTime expiration;

  private Boolean active;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Stock stock;

    @PrePersist
    public void prePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }
}
