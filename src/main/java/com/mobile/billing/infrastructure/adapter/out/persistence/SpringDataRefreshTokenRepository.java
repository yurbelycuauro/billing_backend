package com.mobile.billing.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.RefreshTokenEntity;

public interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    java.util.Optional<RefreshTokenEntity> findByToken(String token);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query("update RefreshTokenEntity refreshToken "
	    + "set refreshToken.token = :replacementToken, refreshToken.expiryDate = :expiryDate "
	    + "where refreshToken.token = :currentToken and refreshToken.tenantId = :tenantId")
    int rotateToken(
	    @org.springframework.data.repository.query.Param("currentToken") String currentToken,
	    @org.springframework.data.repository.query.Param("replacementToken") String replacementToken,
	    @org.springframework.data.repository.query.Param("expiryDate") java.time.Instant expiryDate,
	    @org.springframework.data.repository.query.Param("tenantId") Long tenantId);
}
