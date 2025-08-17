package com.multitenant.template.config;

import com.multitenant.template.tenant.context.TenantContext;
import com.multitenant.template.tenant.model.TenantData;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class PostgresRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        TenantData tenant = TenantContext.getTenantOrNull();
        return (tenant != null) ? tenant.getTenantId() : null;
    }
}
