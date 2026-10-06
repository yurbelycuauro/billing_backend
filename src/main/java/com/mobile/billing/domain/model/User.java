package com.mobile.billing.domain.model;

import java.util.EnumSet;
import java.util.Set;

public class User {

    private final Long id;
    private Long tenantId;
    private final String username;
    private String passwordHash;
    private final Set<UserRole> roles;
    private boolean active;

    public User(Long id, Long tenantId, String username, String passwordHash, Set<UserRole> roles, boolean active) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("El usuario debe tener al menos un rol.");
        }
        this.id = id;
        this.tenantId = tenantId;
        this.username = username.trim();
        this.passwordHash = passwordHash;
        this.roles = EnumSet.copyOf(roles);
        this.active = active;
    }

    public static User create(Long tenantId, String username, String passwordHash, Set<UserRole> roles) {
        return new User(null, tenantId, username, passwordHash, roles, true);
    }

    public void update(String passwordHash, Set<UserRole> roles) {
        if (passwordHash != null && !passwordHash.isBlank()) {
            this.passwordHash = passwordHash;
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("El usuario debe tener al menos un rol.");
        }
        this.roles.clear();
        this.roles.addAll(roles);
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Set<UserRole> getRoles() { return Set.copyOf(roles); }
    public boolean isActive() { return active; }
}