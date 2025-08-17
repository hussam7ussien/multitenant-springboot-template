package com.multitenant.template.config;

import com.multitenant.template.tenant.model.PostgresConnection;
import com.multitenant.template.tenant.model.TenantData;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class DataSourceConfig {
    private final TenantConfigurationProperties tenantConfig;
    public DataSourceConfig(TenantConfigurationProperties tenantConfig) {
        this.tenantConfig = tenantConfig;
    }

    @Bean
    public DataSource dataSource(){
        Map<Object,Object> tenantDataSources = tenantConfig.getTenantsData().entrySet().stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> buildDataSource(entry.getValue())
                        )
                );

        PostgresRoutingDataSource routingDataSource = new PostgresRoutingDataSource();
        routingDataSource.setTargetDataSources(tenantDataSources);
        routingDataSource.setDefaultTargetDataSource(
                tenantDataSources.values().stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No tenants configured"))
        );
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    private DataSource buildDataSource(TenantData tenantData){
        PostgresConnection pg = tenantData.getPostgresConnection();
        return DataSourceBuilder.create()
                .url(pg.getUrl())
                .username(pg.getUsername())
                .password(pg.getPassword())
                .driverClassName(pg.getDriverClassName())
                .build();
    }
}
