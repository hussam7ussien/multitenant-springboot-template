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
    private UserEntity customer;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private BranchEntity branch;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String orderMode; // eat-in, delivery, takeaway

    private String orderCode;
    private String deliveryAddress;
    
    // QR Code URL for eat-in orders (null for delivery/takeaway)
    private String qrCodeUrl;
    
    private BigDecimal subtotal;
    private BigDecimal vat;
    private BigDecimal total;
    private BigDecimal discount;
    private BigDecimal specialDiscount;
    private BigDecimal coversFee;
    
    private String paymentMethod;
    private String promoCode;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
