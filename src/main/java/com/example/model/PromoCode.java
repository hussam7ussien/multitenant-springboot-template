package com.multitenant.menu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromoCode {
    private Long id;
    private String code;
    private BigDecimal discount;
    private String discountType;
    private Boolean valid;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Boolean isWelcomeCoupon;
}
