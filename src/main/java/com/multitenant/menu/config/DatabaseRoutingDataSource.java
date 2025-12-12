package com.multitenant.menu.config;

import com.multitenant.menu.tenant.context.TenantContext;
import com.multitenant.menu.tenant.model.TenantData;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DatabaseRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        TenantData tenant = TenantContext.getTenantOrNull();
        return (tenant != null) ? tenant.getTenantId() : null;
    }
}
