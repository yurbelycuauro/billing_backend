package com.mobile.billing.config.security;

import com.mobile.billing.infrastructure.adapter.out.persistence.tenant.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ADMIN_TENANT_HEADER = "X-Admin-Tenant-Id";
    private static final String ADMIN_ROLE = "ADMIN";

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);

            if (token != null && jwtProvider.validate(token)) {
                Long tenantId = resolveTenantId(request, token);

                if (tenantId != null) {
                    TenantContextHolder.setCurrentTenant(tenantId);
                    setSecurityContext(request, token);
                }
            }

            // filterChain DEBE ejecutarse dentro del bloque protegido
            filterChain.doFilter(request, response);

        } finally {
            // Se garantiza la limpieza de hilos (ThreadLocal) al finalizar la petición HTTP
            // completa
            TenantContextHolder.clear();
            SecurityContextHolder.clearContext();
        }
    }

    private Long resolveTenantId(HttpServletRequest request, String token) {
        Long tenantId = jwtProvider.getTenantId(token);
        Set<String> roles = jwtProvider.getRoles(token);
        boolean admin = roles.contains(ADMIN_ROLE);
        boolean adminTenantHeaderSent = request.getHeader(ADMIN_TENANT_HEADER) != null;

        logger.info("JWT request: admin=" + admin + ", " + ADMIN_TENANT_HEADER + " enviado="
                + adminTenantHeaderSent);

        if (admin) {
            return extractAdminTenantId(request, tenantId);
        }

        return tenantId;
    }

    private Long extractAdminTenantId(HttpServletRequest request, Long defaultTenantId) {
        String adminTenantHeader = request.getHeader(ADMIN_TENANT_HEADER);

        if (StringUtils.hasText(adminTenantHeader)) {
            try {
                return Long.parseLong(adminTenantHeader.trim());
            } catch (NumberFormatException e) {
                logger.warn("Header X-Admin-Tenant-Id no válido. Se usará el tenant por defecto.");
            }
        }

        return defaultTenantId;
    }

    private void setSecurityContext(HttpServletRequest request, String token) {
        String subject = jwtProvider.getSubject(token);
        Set<String> roles = jwtProvider.getRoles(token);

        var authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(subject, null,
                authorities);

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}