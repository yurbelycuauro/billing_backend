package com.mobile.billing.domain.model;

import java.util.UUID;

import lombok.Getter;

@Getter
public class UserTenantPermission {
    private UUID keycloakUserId;
 
    private final Long tenantId;

    public UserTenantPermission(UUID keycloakUserId, Long tenantId) {
        this.keycloakUserId = keycloakUserId;
        this.tenantId = tenantId;
    }

    
}
