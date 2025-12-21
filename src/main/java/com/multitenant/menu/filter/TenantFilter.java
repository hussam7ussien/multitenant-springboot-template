package com.multitenant.menu.filter;

import com.multitenant.menu.tenant.context.TenantContext;
import com.multitenant.menu.tenant.model.TenantData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class TenantFilter extends OncePerRequestFilter {
    private final Set<String> validTenants;

    public TenantFilter(Set<String> validTenants) {
        this.validTenants = validTenants;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Skip tenant validation for CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Skip tenant validation for public endpoints
        if (shouldSkipTenantValidation(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String tenantId = request.getHeader("X-Tenant-ID");
        if (tenantId == null || tenantId.isBlank()) {
            log.error("Missing X-Tenant-ID header for path: {}", path);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "X-Tenant-ID header is required");
            return;
        }

        if (!validTenants.contains(tenantId)) {
            log.error("Invalid tenantId received: {}", tenantId);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid tenant: " + tenantId);
            return;
        }

        TenantContext.setTenant(
                TenantData.builder()
                        .tenantId(tenantId).build()
        );

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
    
    private boolean shouldSkipTenantValidation(String path) {
        // Skip tenant validation for Swagger UI, API docs, and public endpoints
        return path.equals("/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars") ||
               path.startsWith("/actuator") ||
               path.equals("/favicon.ico") ||
               path.equals("/error") ||
               path.equals("/api/v1/auth/login"); // Only skip login, OTP endpoints need tenant
    }
}
