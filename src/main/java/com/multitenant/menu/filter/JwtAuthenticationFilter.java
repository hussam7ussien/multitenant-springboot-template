package com.multitenant.menu.filter;

import com.multitenant.menu.entity.sql.UserEntity;
import com.multitenant.menu.services.JwtService;
import com.multitenant.menu.services.UserService;
import com.multitenant.menu.tenant.context.TenantContext;
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
            // Extract JWT token, handling potential double "Bearer " prefix
            String jwt = authHeader.substring(7).trim(); // Remove "Bearer "
            // If token still starts with "Bearer", remove it (handles "Bearer Bearer..." case)
            if (jwt.toLowerCase().startsWith("bearer")) {
                jwt = jwt.substring(6).trim(); // Remove "Bearer" (case-insensitive)
                log.warn("Detected double 'Bearer' prefix in Authorization header, cleaned token");
            }
            final String finalJwt = jwt;
            final String username = jwtService.extractUsername(finalJwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Extract tenant_id from token
                String tokenTenantId = jwtService.extractTenantId(finalJwt);
                String requestTenantId = request.getHeader("X-Tenant-ID");
                
                // Validate tenant_id matches (prevent cross-tenant access)
                if (tokenTenantId != null && requestTenantId != null && !tokenTenantId.equals(requestTenantId)) {
                    log.warn("Tenant mismatch: token tenant={}, request tenant={}", tokenTenantId, requestTenantId);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Tenant mismatch");
                    return;
                }
                
                // Check if token is expired first
                if (jwtService.isTokenExpired(finalJwt)) {
                    log.warn("JWT token expired for username: {}", username);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                    return;
                }
                
                // Validate token
                if (jwtService.validateToken(finalJwt, username)) {
                    // Extract user_id from token (preferred method)
                    Long userId = jwtService.extractUserId(finalJwt);
                    UserEntity user = null;
                    
                    // Verify TenantContext is set (required for database routing)
                    try {
                        String currentTenantId = TenantContext.getTenant() != null 
                            ? TenantContext.getTenant().getTenantId() 
                            : null;
                        
                        if (currentTenantId == null) {
                            log.error("TenantContext not set when looking up user. Token tenant: {}, Request tenant: {}", 
                                    tokenTenantId, requestTenantId);
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Tenant context not initialized");
                            return;
                        }
                        
                        if (!currentTenantId.equals(tokenTenantId)) {
                            log.error("TenantContext mismatch: context tenant={}, token tenant={}, request tenant={}", 
                                    currentTenantId, tokenTenantId, requestTenantId);
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Tenant context mismatch");
                            return;
                        }
                        
                        log.debug("Looking up user - userId: {}, username: {}, tenant: {}", userId, username, currentTenantId);
                        
                        if (userId != null) {
                            // Find user by ID (most reliable)
                            user = userService.findById(userId).orElse(null);
                            if (user == null) {
                                log.warn("User not found by ID: {} in tenant: {}", userId, currentTenantId);
                            } else {
                                log.debug("User found by ID: {} - username: {}", userId, user.getUsername());
                            }
                        }
                        
                        // Fallback to username lookup if user_id not available or user not found by ID
                        if (user == null) {
                            log.debug("Attempting username lookup: {} in tenant: {}", username, currentTenantId);
                            user = userService.findByUsername(username).orElse(null);
                            if (user == null) {
                                log.warn("User not found by username: {} in tenant: {}", username, currentTenantId);
                            } else {
                                log.debug("User found by username: {} - ID: {}", username, user.getId());
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error looking up user - userId: {}, username: {}, tenant: {}", 
                                userId, username, tokenTenantId, e);
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error authenticating user");
                        return;
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
                        log.info("User authenticated: {} (ID: {}, tenant: {})", username, userId, tokenTenantId);
                    } else {
                        log.error("User not found - username: {}, userId from token: {}, tenant: {}. " +
                                "This may indicate the user was deleted or the token is from a different tenant database.", 
                                username, userId, tokenTenantId);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                        return;
                    }
                } else {
                    log.warn("JWT token validation failed for username: {}", username);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                    return;
                }
            }
        } catch (Exception e) {
            log.error("JWT validation failed", e);
        }

        filterChain.doFilter(request, response);
    }
}

