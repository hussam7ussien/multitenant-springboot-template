package com.multitenant.menu.config;

import com.multitenant.menu.tenant.model.DatabaseConnection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
                                entry -> buildDatabaseDataSource(entry.getValue().getDatabaseConnection(), entry.getKey())
                        )
                );
    }
    
    private DataSource buildDatabaseDataSource(DatabaseConnection db, String tenantId){
        // Create HikariConfig for proper connection pool management
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(db.getUrl());
        config.setUsername(db.getUsername());
        config.setPassword(db.getPassword());
        config.setDriverClassName(db.getDriverClassName());
        
        // Connection pool settings - adjust based on your needs
        config.setMaximumPoolSize(5);  // Max 5 connections per tenant
        config.setMinimumIdle(2);      // Keep 2 idle connections ready
        config.setConnectionTimeout(30000);  // 30 seconds to get connection
        config.setIdleTimeout(600000);      // 10 minutes idle timeout
        config.setMaxLifetime(1800000);      // 30 minutes max connection lifetime
        config.setLeakDetectionThreshold(60000);  // Detect leaks after 60 seconds
        config.setPoolName("HikariPool-" + tenantId);  // Named pool for monitoring
        
        // Connection validation
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);  // 5 seconds validation timeout
        
        // Additional MySQL-specific optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        return new HikariDataSource(config);
    }
}
