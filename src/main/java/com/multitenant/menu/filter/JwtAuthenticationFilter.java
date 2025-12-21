package com.multitenant.menu.filter;

import com.multitenant.menu.entity.sql.UserEntity;
import com.multitenant.menu.services.JwtService;
import com.multitenant.menu.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        
        // Skip JWT validation for public endpoints
        String path = request.getRequestURI();
        if (path.equals("/") ||
            path.startsWith("/api/v1/auth/") ||
            path.startsWith("/api/v1/orders/signup") ||
            path.startsWith("/api/v1/users/signup") ||
            path.startsWith("/api/v1/users/signin") ||
            path.startsWith("/api/v1/auth/otp/") ||
            path.startsWith("/api/v1/auth/refresh") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-resources") ||
            path.startsWith("/webjars") ||
            path.startsWith("/actuator") ||
            path.equals("/favicon.ico") ||
            path.equals("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Extract tenant_id from token
                String tokenTenantId = jwtService.extractTenantId(jwt);
                String requestTenantId = request.getHeader("X-Tenant-ID");
                
                // Validate tenant_id matches (prevent cross-tenant access)
                if (tokenTenantId != null && requestTenantId != null && !tokenTenantId.equals(requestTenantId)) {
                    log.warn("Tenant mismatch: token tenant={}, request tenant={}", tokenTenantId, requestTenantId);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Tenant mismatch");
                    return;
                }
                
                // Validate token
                if (jwtService.validateToken(jwt, username)) {
                    // Extract user_id from token (preferred method)
                    Long userId = jwtService.extractUserId(jwt);
                    UserEntity user = null;
                    
                    if (userId != null) {
                        // Find user by ID (most reliable)
                        user = userService.findById(userId).orElse(null);
                    }
                    
                    // Fallback to username lookup if user_id not available
                    if (user == null) {
                        user = userService.findByUsername(username).orElse(null);
                    }
                    
                    if (user != null) {
                        // Set user as principal
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("User authenticated: {} (ID: {}, tenant: {})", username, userId, tokenTenantId);
                    } else {
                        log.warn("User not found - username: {}, userId from token: {}", username, userId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT validation failed", e);
        }

        filterChain.doFilter(request, response);
    }
}

