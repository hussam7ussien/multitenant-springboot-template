package com.multitenant.menu.mapper;

import org.mapstruct.Mapper;
import com.multitenant.menu.model.Category;
import com.multitenant.menu.entity.sql.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toApiModel(CategoryEntity entity);
    CategoryEntity toEntity(Category apiModel);
}
