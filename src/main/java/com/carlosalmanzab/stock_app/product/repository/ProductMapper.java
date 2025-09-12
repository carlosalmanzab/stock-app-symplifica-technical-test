package com.carlosalmanzab.stock_app.product.repository;

import com.carlosalmanzab.stock_app.product.controller.ProductCreateDto;
import com.carlosalmanzab.stock_app.product.controller.ProductUpdateDto;
import com.carlosalmanzab.stock_app.product.model.Product;
import com.carlosalmanzab.stock_app.product.repository.projection.ProductView;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductView toView(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "stock", ignore = true)
    Product toProduct(ProductCreateDto dto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "movements", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void UpdateFromDto(ProductUpdateDto dto, @MappingTarget Product product);
}
