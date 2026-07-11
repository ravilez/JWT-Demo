package com.example.demo.security;

import com.example.demo.model.User;
import com.example.demo.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private JwtUtil jwtUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUsernameFromToken(jwt);
                String email = jwtUtils.getEmailFromToken(jwt);
                String tenantId = jwtUtils.getTenantIdFromToken(jwt);

            
                if (username != null && tenantId != null) {
                    // 2. Hydrate Principal and set Spring Security Context
                    CustomUserPrincipal principal = new CustomUserPrincipal(username, email, tenantId);
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 3. Hydrate Tenant Context for your dynamic Hibernate connection provider
                    //TenantContext.setCurrentTenant(tenantId);
                }            
            
            }
        } catch (Exception e) {
            LOGGER.error("Cannot set user authentication", e);
            // Token invalid, expired, or tampered with. Fail silently to let SecurityConfig handle anonymous access denials.
            SecurityContextHolder.clearContext();
        }

        try {
        	filterChain.doFilter(request, response);
        } finally {
            // Crucial: Wipe both contexts to completely eliminate risk of thread-pool data cross-contamination
            //TenantContext.clear();
            SecurityContextHolder.clearContext();
        }    
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        LOGGER.info("Authorization header present: {}", headerAuth != null);
        if (headerAuth != null) {
            LOGGER.info("Authorization header value: {}", headerAuth);
        }

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
