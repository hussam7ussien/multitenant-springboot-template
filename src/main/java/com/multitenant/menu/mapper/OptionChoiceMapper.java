package com.multitenant.menu.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OptionChoiceMapper {
    com.multitenant.menu.model.OptionChoice toApiModel(com.multitenant.menu.entity.sql.OptionChoiceEntity entity);
    com.multitenant.menu.entity.sql.OptionChoiceEntity toEntity(com.multitenant.menu.model.OptionChoice apiModel);
}
