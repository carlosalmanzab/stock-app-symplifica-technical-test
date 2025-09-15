package com.carlosalmanzab.stock_app.product.repository;

import com.carlosalmanzab.stock_app.product.controller.ProductCreateDto;
import com.carlosalmanzab.stock_app.product.controller.ProductUpdateDto;
import com.carlosalmanzab.stock_app.product.model.Product;
import com.carlosalmanzab.stock_app.product.repository.projection.ProductProjection;
import com.carlosalmanzab.stock_app.product.repository.projection.ProductView;
import org.mapstruct.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

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
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void UpdateFromDto(ProductUpdateDto dto, @MappingTarget Product product);

    ProductView toView(ProductProjection projection);

    default UUID map (String string) {
        return UUID.fromString(string);
    }

    default Timestamp map(LocalDateTime localDateTime) {
        return localDateTime != null ? Timestamp.valueOf(localDateTime) : null;
    }

    default LocalDateTime map(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}
