package com.multitenant.menu.entity.sql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private BranchEntity branch;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String orderMode; // eat-in, delivery, takeaway

    private String deliveryAddress;
    private BigDecimal total;
    private String couponCode;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItemEntity> items;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
