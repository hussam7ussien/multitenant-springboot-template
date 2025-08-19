package com.multitenant.template.config;

import com.multitenant.template.tenant.model.PostgresConnection;
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
        Map<String,DataSource> tenantPostgresConnections = buildTenantDataSource();
        if(tenantPostgresConnections.isEmpty()){
            throw new IllegalStateException("No tenants configured");
        }

        //convert Map<String, DataSource> to Map<Object, Object>
        Map<Object, Object> targetDataSources = new HashMap<>(tenantPostgresConnections);
        PostgresRoutingDataSource routingDataSource = new PostgresRoutingDataSource();

        //Set target data sources
        routingDataSource.setTargetDataSources(targetDataSources);

        //set first tenant as default data source
        DataSource defaultDataSource = tenantPostgresConnections.values().iterator().next();
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);

        //validate and finalize setup
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }

    private Map<String, DataSource> buildTenantDataSource(){
        return  tenantConfig.getTenantsData().entrySet().stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> buildPostgresDataSource(entry.getValue().getPostgresConnection())
                        )
                );
    }
    private DataSource buildPostgresDataSource(PostgresConnection pg){
        return DataSourceBuilder.create()
                .url(pg.getUrl())
                .username(pg.getUsername())
                .password(pg.getPassword())
                .driverClassName(pg.getDriverClassName())
                .build();
    }
}
