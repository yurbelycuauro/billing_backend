package com.mobile.billing.infrastructure.adapter.in.rest;

import com.mobile.billing.domain.ports.in.AuthenticateUserUseCase;
import com.mobile.billing.domain.ports.in.RefreshTokenUseCase;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.LoginResponse;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.RefreshTokenRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUser;
    private final RefreshTokenUseCase refreshTokens;

    public AuthController(AuthenticateUserUseCase authenticateUser, RefreshTokenUseCase refreshTokens) {
        this.authenticateUser = authenticateUser;
        this.refreshTokens = refreshTokens;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(LoginResponse.from(authenticateUser.execute(request.toCommand())));
        } catch (BadCredentialsException | IllegalArgumentException exception) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh request received test");
        try {
            var result = refreshTokens.refresh(request.refreshToken());
            var response = LoginResponse.from(result);
            log.info(
                    "Refresh request succeeded: username={}, tenantId={}, tokenType={}, expiresIn={}, "
                            + "accessTokenPresent={}, refreshTokenPresent={}",
                    response.username(),
                    response.tenantId(),
                    response.tokenType(),
                    response.expiresIn(),
                    response.token() != null && !response.token().isBlank(),
                    response.refreshToken() != null && !response.refreshToken().isBlank());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException exception) {
            log.warn("Refresh request rejected: {}", exception.getMessage());
            return ResponseEntity.status(401).build();
        }
    }
}