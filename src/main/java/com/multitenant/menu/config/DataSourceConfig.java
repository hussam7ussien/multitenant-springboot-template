package com.multitenant.menu.config;

import com.multitenant.menu.tenant.model.DatabaseConnection;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
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
        //First step build tenant data sources
        Map<String,DataSource> tenantDatabaseConnections = buildTenantDataSource();
        if(tenantDatabaseConnections.isEmpty()){
            throw new IllegalStateException("No tenants configured");
        }

        //convert Map<String, DataSource> to Map<Object, Object>
        Map<Object, Object> targetDataSources = new HashMap<>(tenantDatabaseConnections);
        DatabaseRoutingDataSource routingDataSource = new DatabaseRoutingDataSource();

        //Set target data sources
        routingDataSource.setTargetDataSources(targetDataSources);

        //set first tenant as default data source
        DataSource defaultDataSource = tenantDatabaseConnections.values().iterator().next();
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);

        //validate and finalize setup
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }

    private Map<String, DataSource> buildTenantDataSource(){
        if (tenantConfig.getTenantsData() == null) {
            throw new IllegalStateException("Tenants data is not configured");
        }
        return tenantConfig.getTenantsData().entrySet().stream()
                .collect(
                        Collectors.toMap(
                                entry -> entry.getKey(),
                                entry -> buildDatabaseDataSource(entry.getValue().getDatabaseConnection())
                        )
                );
    }
    private DataSource buildDatabaseDataSource(DatabaseConnection db){
        return DataSourceBuilder.create()
                .url(db.getUrl())
                .username(db.getUsername())
                .password(db.getPassword())
                .driverClassName(db.getDriverClassName())
                .build();
    }
}
