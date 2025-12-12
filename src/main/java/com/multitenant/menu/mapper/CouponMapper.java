package com.multitenant.menu.mapper;

import com.multitenant.menu.model.PromoCode;
import com.multitenant.menu.entity.sql.CouponEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CouponMapper {
    @Mapping(source = "code", target = "code")
    @Mapping(source = "discount", target = "discount")
    @Mapping(source = "discountType", target = "discountType")
    @Mapping(source = "valid", target = "valid")
    @Mapping(source = "validFrom", target = "validFrom")
    @Mapping(source = "validTo", target = "validTo")
    @Mapping(source = "isWelcomeCoupon", target = "isWelcomeCoupon")
    PromoCode toApiModel(CouponEntity entity);

    @Mapping(source = "code", target = "code")
    @Mapping(source = "discount", target = "discount")
    @Mapping(source = "discountType", target = "discountType")
    @Mapping(source = "valid", target = "valid")
    @Mapping(source = "validFrom", target = "validFrom")
    @Mapping(source = "validTo", target = "validTo")
    @Mapping(source = "isWelcomeCoupon", target = "isWelcomeCoupon")
    CouponEntity toEntity(PromoCode apiModel);
}
