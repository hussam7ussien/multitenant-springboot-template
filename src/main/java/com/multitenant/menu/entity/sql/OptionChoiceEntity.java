package com.multitenant.menu.entity.sql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "option_choices")
@Getter
@Setter
public class OptionChoiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal priceModifier = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private ProductOptionEntity option;
}
