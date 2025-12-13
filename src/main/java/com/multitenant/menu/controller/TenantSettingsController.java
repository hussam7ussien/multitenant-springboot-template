package com.multitenant.menu.controller;

import com.multitenant.menu.api.TenantSettingsApi;
import com.multitenant.menu.model.TenantSettingsDTO;
import com.multitenant.menu.model.TenantSettingUpdateRequest;
import com.multitenant.menu.services.TenantSettingsService;
import com.multitenant.menu.tenant.context.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import jakarta.validation.Valid;

/**
 * Tenant Settings Controller
 * Implements TenantSettingsApi generated from OpenAPI specification
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TenantSettingsController implements TenantSettingsApi {
    private final TenantSettingsService tenantSettingsService;

    /**
     * Get all tenant settings as key-value pairs
     * Defined in OpenAPI: GET /tenant-settings
     */
    @Override
    public ResponseEntity<Map<String, String>> getAllTenantSettings() {
        String tenantId = TenantContext.getTenant() != null ? TenantContext.getTenant().getTenantId() : "tenant1";
        Map<String, String> settings = tenantSettingsService.getAllSettings();
        return ResponseEntity.ok(settings);
    }

    /**
     * Get a specific setting by key
     * Defined in OpenAPI: GET /tenant-settings/{key}
     */
    @Override
    public ResponseEntity<String> getTenantSetting(@PathVariable String key) {
        String tenantId = TenantContext.getTenant() != null ? TenantContext.getTenant().getTenantId() : "tenant1";
        return tenantSettingsService.getSetting(key)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Update a specific setting
     * Defined in OpenAPI: PUT /tenant-settings/{key}
     */
    @Override
    public ResponseEntity<TenantSettingsDTO> updateTenantSetting(
            @PathVariable String key,
            @Valid @RequestBody TenantSettingUpdateRequest tenantSettingUpdateRequest) {
        String tenantId = TenantContext.getTenant() != null ? TenantContext.getTenant().getTenantId() : "tenant1";
        String value = tenantSettingUpdateRequest.getValue();
        String dataType = tenantSettingUpdateRequest.getDataType() != null ? 
                tenantSettingUpdateRequest.getDataType().toString() : "string";
        
        var saved = tenantSettingsService.saveSetting(key, value, dataType);
        
        // Build the model DTO
        var modelDto = new TenantSettingsDTO();
        modelDto.setId(Math.toIntExact(saved.getId()));
        modelDto.setKey(saved.getKey());
        modelDto.setValue(saved.getValue());
        modelDto.setDataType(TenantSettingsDTO.DataTypeEnum.fromValue(saved.getDataType()));
        
        return ResponseEntity.ok(modelDto);
    }

    /**
     * Delete a setting
     * Defined in OpenAPI: DELETE /tenant-settings/{key}
     */
    @Override
    public ResponseEntity<Void> deleteTenantSetting(@PathVariable String key) {
        String tenantId = TenantContext.getTenant() != null ? TenantContext.getTenant().getTenantId() : "tenant1";
        tenantSettingsService.deleteSetting(key);
        return ResponseEntity.noContent().build();
    }
}
