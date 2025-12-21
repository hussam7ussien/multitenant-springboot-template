package com.multitenant.menu.repository.sql;

import com.multitenant.menu.entity.sql.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByOrderCode(String orderCode);

    @EntityGraph(attributePaths = {"items", "items.product"})
    @Query("SELECT o FROM OrderEntity o WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC")
    Page<OrderEntity> findByCustomerIdOrderByCreatedAtDesc(@Param("customerId") Long customerId, Pageable pageable);
}

