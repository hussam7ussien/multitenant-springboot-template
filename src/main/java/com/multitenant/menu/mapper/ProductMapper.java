package com.multitenant.menu.mapper;

import org.mapstruct.Mapper;
import com.multitenant.menu.model.Product;
import com.multitenant.menu.entity.sql.ProductEntity;

@Mapper(uses = ProductOptionMapper.class, componentModel = "spring")
public interface ProductMapper {
    Product toApiModel(ProductEntity entity);
    ProductEntity toEntity(Product apiModel);
}
