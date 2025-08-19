package com.multitenant.template.config;

import com.multitenant.template.tenant.model.PostgresConnection;
import com.multitenant.template.tenant.model.TenantData;
import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class FlywayConfig {

    private final TenantConfigurationProperties tenantConfig;

    public FlywayConfig(TenantConfigurationProperties tenantConfig) {
        this.tenantConfig = tenantConfig;
    }

    @Bean
    public ApplicationRunner migrateTenants() {
        return args -> {
            for (Map.Entry<String, TenantData> tenantEntry : tenantConfig.getTenantsData().entrySet()) {
                String tenantId = tenantEntry.getKey();
                PostgresConnection conn = tenantEntry.getValue().getPostgresConnection();

                // Run Flyway for each tenant
                Flyway flyway = Flyway.configure()
                        .dataSource(conn.getUrl(), conn.getUsername(), conn.getPassword())
                        .locations("classpath:db/migration") // same migrations for all tenants
                        .schemas("public")
                        .load();

                flyway.migrate();
                System.out.println("✅ Flyway migration applied for tenant: " + tenantId);
            }
        };
    }
}