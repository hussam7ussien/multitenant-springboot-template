package com.multitenant.menu.entity.sql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "branches")
@Getter
@Setter
public class BranchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;
    private String city;
    private String area;
    private String phone;

    // Order mode flags - which order modes are enabled for this branch
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean eatInEnabled = true;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean deliveryEnabled = true;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean takeawayEnabled = true;
}
