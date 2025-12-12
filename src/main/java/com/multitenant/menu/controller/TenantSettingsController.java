package com.multitenant.menu.controller;

import com.multitenant.menu.dto.TenantSettingsDTO;
import com.multitenant.menu.services.TenantSettingsService;
import com.multitenant.menu.tenant.context.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TenantSettingsController {
    private final TenantSettingsService tenantSettingsService;

    /**
     * Get all tenant settings as key-value pairs
     */
    @GetMapping("/tenant-settings")
    public ResponseEntity<Map<String, String>> getAllTenantSettings() {
        String tenantId = TenantContext.getTenant() != null ? TenantContext.getTenant().getTenantId() : "tenant1";
        Map<String, String> settings = tenantSettingsService.getAllSettings();
        return ResponseEntity.ok(settings);
    }

    /**
     * Get a specific setting by key
     */
    @GetMapping("/tenant-settings/{key}")
    public ResponseEntity<String> getTenantSetting(@PathVariable String key) {
        String tenantId = TenantContext.getTenant() != null ? TenantContext.getTenant().getTenantId() : "tenant1";
        return tenantSettingsService.getSetting(key)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Update a specific setting
     */
    @PutMapping("/tenant-settings/{key}")
    public ResponseEntity<TenantSettingsDTO> updateTenantSetting(
            @PathVariable String key,
            @RequestBody Map<String, String> body) {
        String tenantId = TenantContext.getTenant() != null ? TenantContext.getTenant().getTenantId() : "tenant1";
        String value = body.get("value");
        String dataType = body.getOrDefault("dataType", "string");
        
        var saved = tenantSettingsService.saveSetting(key, value, dataType);
        return ResponseEntity.ok(tenantSettingsService.toDTO(saved));
    }

    /**
     * Delete a setting
     */
    @DeleteMapping("/tenant-settings/{key}")
    public ResponseEntity<Void> deleteTenantSetting(@PathVariable String key) {
        String tenantId = TenantContext.getTenant() != null ? TenantContext.getTenant().getTenantId() : "tenant1";
        tenantSettingsService.deleteSetting(key);
        return ResponseEntity.noContent().build();
    }
}
