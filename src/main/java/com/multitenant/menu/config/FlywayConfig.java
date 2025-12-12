package com.multitenant.menu.config;

import com.multitenant.menu.tenant.model.DatabaseConnection;
import com.multitenant.menu.tenant.model.TenantData;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Slf4j
public class FlywayConfig implements ApplicationListener<ApplicationReadyEvent> {

    private final TenantConfigurationProperties tenantConfig;

    public FlywayConfig(TenantConfigurationProperties tenantConfig) {
        this.tenantConfig = tenantConfig;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("🚀 Starting Flyway migrations for all tenants...");
        migrateTenantDatabases();
    }

    private void migrateTenantDatabases() {
        try {
            Map<String, TenantData> tenantsData = tenantConfig.getTenantsData();
            log.info("📋 Found {} tenants to migrate", tenantsData.size());
            
            for (Map.Entry<String, TenantData> tenantEntry : tenantsData.entrySet()) {
                String tenantId = tenantEntry.getKey();
                TenantData tenantData = tenantEntry.getValue();
                DatabaseConnection conn = tenantData.getDatabaseConnection();

                try {
                    log.info("📦 Running Flyway migrations for tenant: {}", tenantId);
                    log.debug("   Database URL: {}", conn.getUrl());
                    
                    // Run Flyway for each tenant
                    Flyway flyway = Flyway.configure()
                            .dataSource(conn.getUrl(), conn.getUsername(), conn.getPassword())
                            .locations("classpath:db/migration")
                            .load();

                    flyway.migrate();
                    log.info("✅ Flyway migration completed successfully for tenant: {}", tenantId);
                } catch (Exception e) {
                    log.error("❌ Error running Flyway migrations for tenant: {}", tenantId, e);
                }
            }
        } catch (Exception e) {
            log.error("❌ Critical error during tenant migration initialization", e);
        }
    }
}