package com.carlosalmanzab.stock_app.product.repository;

import com.carlosalmanzab.stock_app.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    <T> Optional<T> findByUuid(UUID uuid, Class<T> type);
    Optional<Product> findByUuid(UUID uuid);
    <T> Page<T> findAllBy(Pageable pageable, Class<T> type);
}
