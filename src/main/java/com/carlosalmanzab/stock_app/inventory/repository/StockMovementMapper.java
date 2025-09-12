package com.carlosalmanzab.stock_app.inventory.repository;

import com.carlosalmanzab.stock_app.inventory.model.StockMovement;
import com.carlosalmanzab.stock_app.inventory.repository.projection.StockMovementView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {
    StockMovementView toView(StockMovement movement);
}
