package com.multitenant.template.filter;

import com.multitenant.template.tenant.context.TenantContext;
import com.multitenant.template.tenant.model.TenantData;
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
        String tenantId = request.getHeader("X-Tenant-ID");
        if (tenantId == null || tenantId.isBlank()) {
            log.error("Missing X-Tenant-ID header");
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
            filterChain.doFilter(request,response);
        }finally {
            TenantContext.clear();
        }
    }
}
