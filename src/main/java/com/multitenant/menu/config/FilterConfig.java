package com.multitenant.menu.config;

import com.multitenant.menu.filter.TenantFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class FilterConfig {
    private final TenantConfigurationProperties tenantConfig;

    public FilterConfig(TenantConfigurationProperties tenantConfig) {
        this.tenantConfig = tenantConfig;
    }

    @Bean
    public FilterRegistrationBean<TenantFilter> tenantFilter() {
        Set<String> validTenantIds = tenantConfig.getTenantsData().keySet();
        FilterRegistrationBean<TenantFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TenantFilter(validTenantIds));
        registration.addUrlPatterns("/*");
        registration.setOrder(2);  // Run after CorsFilter (which has order 0)
        return registration;
    }
}
