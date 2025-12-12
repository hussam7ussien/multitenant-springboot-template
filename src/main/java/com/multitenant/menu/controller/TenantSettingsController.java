package com.multitenant.menu.controller;

import com.multitenant.menu.tenant.context.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TenantSettingsController {

    @GetMapping("/tenant-settings")
    public ResponseEntity<Map<String, Object>> getTenantSettings() {
        String tenantId = TenantContext.getTenant() != null ? TenantContext.getTenant().getTenantId() : "tenant1";
        Map<String, Object> settings = new HashMap<>();
        // Provide tenant-specific settings. In a real app these would come from DB.
        if ("tenant1".equals(tenantId)) {
            settings.put("cover_image", "https://via.placeholder.com/1200x400?text=Country+Hills+Cover");
            settings.put("restaurant_name", "Country Hills");
        } else if ("tenant2".equals(tenantId)) {
            settings.put("cover_image", "https://via.placeholder.com/1200x400?text=Tenant2+Cover");
            settings.put("restaurant_name", "Tenant 2 Restaurant");
        } else {
            settings.put("cover_image", "https://via.placeholder.com/1200x400?text=Default+Cover");
            settings.put("restaurant_name", "Restaurant");
        }
        return ResponseEntity.ok(settings);
    }
}
