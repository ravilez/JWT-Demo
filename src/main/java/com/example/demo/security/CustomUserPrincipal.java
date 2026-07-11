package com.example.demo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class CustomUserPrincipal implements UserDetails {
    private final String userId;
    private final String email;
    private final String tenantId;

    public CustomUserPrincipal(String userId, String email, String tenantId) {
        this.userId = userId;
        this.email = email;
        this.tenantId = tenantId;
    }

    public String getUserId() { return userId; }
    public String getTenantId() { return tenantId; }

    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return null; } // Stateless JWTs don't store passwords
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
