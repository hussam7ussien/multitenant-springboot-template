package com.multitenant.template.filter;

import com.multitenant.template.tenant.context.TenantContext;
import com.multitenant.template.tenant.model.TenantData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TenantFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tenantId = request.getHeader("X-Tenant-ID");
        if(tenantId != null){
            TenantContext.setTenant(
                    TenantData.builder()
                            .tenantId(tenantId).build()
            );
        }

        try {
            filterChain.doFilter(request,response);
        }finally {
            TenantContext.clear();
        }
    }
}
