package com.mobile.billing.infrastructure.adapter.out.persistence.entities;

import com.mobile.billing.domain.model.User;
import com.mobile.billing.domain.model.UserRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_user_username", columnNames = { "username" }))
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = EnumSet.noneOf(UserRole.class);

    @Column(nullable = false)
    private boolean active;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private Long tenantId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshTokenEntity> refreshTokens = new ArrayList<>();

    protected UserEntity() {
    }

    public UserEntity(Long id, String username, String passwordHash, Set<UserRole> roles, boolean active) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = EnumSet.copyOf(roles);
        this.active = active;
    }

    public static UserEntity fromDomain(User user) {
        if (user == null) {
            return null;
        }
        UserEntity entity = new UserEntity(user.getId(), user.getUsername(), user.getPasswordHash(), user.getRoles(),
                user.isActive());
        entity.tenantId = user.getTenantId();
        return entity;
    }

    public User toDomain() {
        return new User(id, tenantId, username, passwordHash, roles, active);
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Set<UserRole> getRoles() {
        return Set.copyOf(roles);
    }

    public boolean isActive() {
        return active;
    }
}