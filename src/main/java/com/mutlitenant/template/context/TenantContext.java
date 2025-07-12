package com.mutlitenant.template.context;

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

    public static void clear(){
        CURRENT_TENANT.remove();
    }

}
