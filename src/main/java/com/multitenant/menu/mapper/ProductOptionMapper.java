package com.multitenant.menu.mapper;

import org.mapstruct.Mapper;
import com.multitenant.menu.model.ProductOption;
import com.multitenant.menu.entity.sql.ProductOptionEntity;

@Mapper(uses = OptionChoiceMapper.class, componentModel = "spring")
public interface ProductOptionMapper {
    com.multitenant.menu.model.ProductOption toApiModel(ProductOptionEntity entity);
    ProductOptionEntity toEntity(com.multitenant.menu.model.ProductOption apiModel);
}
