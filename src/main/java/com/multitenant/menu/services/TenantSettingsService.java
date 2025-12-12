package com.multitenant.menu.services;

import com.multitenant.menu.dto.TenantSettingsDTO;
import com.multitenant.menu.entity.sql.TenantSettingsEntity;
import com.multitenant.menu.repository.sql.TenantSettingsRepository;
import com.multitenant.menu.tenant.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantSettingsService {
    private final TenantSettingsRepository tenantSettingsRepository;

    /**
     * Get a single setting by key
     */
    public Optional<String> getSetting(String key) {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Fetching setting '{}' for tenant: {}", key, tenantId);
        return tenantSettingsRepository.findByKey(key).map(TenantSettingsEntity::getValue);
    }

    /**
     * Get a setting by key with default value
     */
    public String getSetting(String key, String defaultValue) {
        return getSetting(key).orElse(defaultValue);
    }

    /**
     * Get all settings as a Map
     */
    public Map<String, String> getAllSettings() {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Fetching all settings for tenant: {}", tenantId);
        List<TenantSettingsEntity> settings = tenantSettingsRepository.findAll();
        Map<String, String> result = new HashMap<>();
        settings.forEach(s -> result.put(s.getKey(), s.getValue()));
        return result;
    }

    /**
     * Save or update a setting
     */
    public TenantSettingsEntity saveSetting(String key, String value, String dataType) {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Saving setting '{}' for tenant: {}", key, tenantId);
        
        Optional<TenantSettingsEntity> existing = tenantSettingsRepository.findByKey(key);
        TenantSettingsEntity entity;
        
        if (existing.isPresent()) {
            entity = existing.get();
            entity.setValue(value);
            entity.setDataType(dataType != null ? dataType : "string");
        } else {
            entity = new TenantSettingsEntity();
            entity.setKey(key);
            entity.setValue(value);
            entity.setDataType(dataType != null ? dataType : "string");
        }
        
        return tenantSettingsRepository.save(entity);
    }

    /**
     * Delete a setting
     */
    public void deleteSetting(String key) {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Deleting setting '{}' for tenant: {}", key, tenantId);
        tenantSettingsRepository.findByKey(key).ifPresent(tenantSettingsRepository::delete);
    }

    /**
     * Convert entity to DTO
     */
    public TenantSettingsDTO toDTO(TenantSettingsEntity entity) {
        if (entity == null) {
            return null;
        }
        return new TenantSettingsDTO(
                entity.getId(),
                entity.getKey(),
                entity.getValue(),
                entity.getDataType()
        );
    }

    /**
     * Convert DTO to entity
     */
    public TenantSettingsEntity toEntity(TenantSettingsDTO dto) {
        if (dto == null) {
            return null;
        }
        TenantSettingsEntity entity = new TenantSettingsEntity();
        entity.setId(dto.getId());
        entity.setKey(dto.getKey());
        entity.setValue(dto.getValue());
        entity.setDataType(dto.getDataType());
        return entity;
    }
}
