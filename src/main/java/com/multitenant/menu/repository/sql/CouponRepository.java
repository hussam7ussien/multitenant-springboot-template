package com.multitenant.menu.repository.sql;

import com.multitenant.menu.entity.sql.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    Optional<CouponEntity> findByCode(String code);
    Optional<CouponEntity> findByIsWelcomeCouponTrue();
}

