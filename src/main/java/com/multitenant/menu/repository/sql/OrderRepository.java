package com.multitenant.menu.repository.sql;

import com.multitenant.menu.entity.sql.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByOrderCode(String orderCode);

    org.springframework.data.domain.Page<OrderEntity> findByCustomerIdOrderByCreatedAtDesc(Long customerId, org.springframework.data.domain.Pageable pageable);
}

