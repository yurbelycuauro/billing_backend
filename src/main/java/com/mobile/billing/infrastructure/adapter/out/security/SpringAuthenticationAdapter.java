package com.mobile.billing.infrastructure.adapter.out.security;

import com.mobile.billing.domain.model.UserRole;
import com.mobile.billing.domain.ports.out.AuthenticationPort;
import com.mobile.billing.domain.ports.out.UserRepositoryPort;
import java.util.Set;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class SpringAuthenticationAdapter implements AuthenticationPort {

    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepository;

    public SpringAuthenticationAdapter(
            AuthenticationManager authenticationManager,
            UserRepositoryPort userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @Override
    public AuthenticatedUser authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        var user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        Set<UserRole> roles = user.getRoles();
        return new AuthenticatedUser(user.getUsername(), user.getTenantId(), roles);
    }
}