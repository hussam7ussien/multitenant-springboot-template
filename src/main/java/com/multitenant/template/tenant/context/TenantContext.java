package com.multitenant.template.tenant.context;

import com.multitenant.template.tenant.model.TenantData;

public class TenantContext {
    public static final ThreadLocal<TenantData> CURRENT_TENANT = new ThreadLocal<>();

    public static void setTenant(TenantData tenantData){
        CURRENT_TENANT.set(tenantData);
    }

    public static TenantData getTenant(){
        TenantData tenant = CURRENT_TENANT.get();
        if(tenant == null){
            throw new IllegalStateException("Tenant context not initialized");
        }
        return tenant;
    }

    public static TenantData getTenantOrNull() {
        return CURRENT_TENANT.get();
    }

    public static void clear(){
        CURRENT_TENANT.remove();
    }

}
