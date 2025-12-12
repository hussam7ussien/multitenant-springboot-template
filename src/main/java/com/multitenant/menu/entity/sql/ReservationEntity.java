package com.multitenant.menu.entity.sql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private BranchEntity branch;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    private Integer covers;
    private Long tableId;
    private String specialRequests;
    private String status = "pending";

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
