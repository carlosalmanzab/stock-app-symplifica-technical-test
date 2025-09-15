package com.carlosalmanzab.stock_app.product.repository;

import com.carlosalmanzab.stock_app.product.model.Product;
import com.carlosalmanzab.stock_app.product.repository.projection.ProductProjection;
import com.carlosalmanzab.stock_app.product.repository.projection.ProductView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  <T> Optional<T> findByUuid(UUID uuid, Class<T> type);

  Optional<Product> findByUuid(UUID uuid);

  <T> Page<T> findAllBy(Pageable pageable, Class<T> type);

  @Query(
      value =
          """
                  SELECT p.uuid as uuid,
                         p.price,
                         p.name,
                         p.description as desc,
                         p.expiration,
                         p.created_at as createdAt
                  FROM product p
                  WHERE similarity(p.name, :name) >= :threshold
                  ORDER BY similarity(p.name, :name) DESC
                  """,
      countQuery =
          """
                  SELECT count(*)
                  FROM product p
                  WHERE similarity(p.name, :name) >= :threshold
                  """,
      nativeQuery = true)
  Page<ProductProjection> findByNameSimilar(
      @Param("name") String name, @Param("threshold") double threshold, Pageable pageable);
}
