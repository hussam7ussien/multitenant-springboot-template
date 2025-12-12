package com.multitenant.menu.repository.sql;

import com.multitenant.menu.entity.sql.TenantSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TenantSettingsRepository extends JpaRepository<TenantSettingsEntity, Long> {
    // Find settings by key
    Optional<TenantSettingsEntity> findByKey(String key);
    
    // Find all settings (useful for admin panel)
    List<TenantSettingsEntity> findAll();
}
