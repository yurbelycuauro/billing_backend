package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import com.mobile.billing.domain.model.User;
import com.mobile.billing.domain.model.UserRole;
import java.util.Set;

public record UserResponse(Long id, Long tenantId, String username, Set<UserRole> roles, boolean active) {
    public static UserResponse fromDomain(User user) {
        return new UserResponse(user.getId(), user.getTenantId(), user.getUsername(), user.getRoles(), user.isActive());
    }
}