package com.mobile.billing.domain.ports.in;

public interface IssueRefreshTokenUseCase {
    String issue(String username, Long tenantId);
}