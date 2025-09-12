package com.carlosalmanzab.stock_app.inventory.repository;

import com.carlosalmanzab.stock_app.inventory.model.Stock;
import com.carlosalmanzab.stock_app.inventory.repository.projection.StockView;
import org.mapstruct.Mapper;

@Mapper( componentModel = "spring")
public interface StockMapper {
    StockView toView(Stock stock);
}
