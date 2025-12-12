package com.multitenant.menu.entity.sql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TenantSettingsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`key`", nullable = false, unique = true, length = 255)
    private String key;

    @Column(name = "`value`", columnDefinition = "LONGTEXT")
    private String value;

    @Column(name = "data_type", length = 50)
    private String dataType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (dataType == null) {
            dataType = "string";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
