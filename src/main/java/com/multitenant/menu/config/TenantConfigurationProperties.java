package com.multitenant.menu.config;

import com.multitenant.menu.tenant.model.TenantData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "tenants")
public class TenantConfigurationProperties {
    private Map<String, TenantData> tenantsData;
}
