package com.multitenant.menu.mapper;

import org.mapstruct.Mapper;
import com.multitenant.menu.model.Branch;
import com.multitenant.menu.entity.sql.BranchEntity;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    Branch toApiModel(BranchEntity entity);
    BranchEntity toEntity(Branch apiModel);
}
