package com.multitenant.menu.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    // TenantFilter is now registered directly in SecurityConfig's filter chain
    // to ensure it runs before JWT authentication and database operations
}
