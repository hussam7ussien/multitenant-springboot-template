package com.multitenant.menu.repository;

import com.multitenant.menu.entity.sql.ProductOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOptionEntity, Long> {
    List<ProductOptionEntity> findByProductId(Long productId);
}
