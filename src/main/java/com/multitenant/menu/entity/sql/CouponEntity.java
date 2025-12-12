package com.multitenant.menu.entity.sql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
public class CouponEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private BigDecimal discount;
    private String discountType; // percentage, fixed
    private Boolean valid = true;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Boolean isWelcomeCoupon = false;
}
