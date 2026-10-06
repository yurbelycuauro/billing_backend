package com.mobile.billing.application;

import com.mobile.billing.domain.model.User;
import com.mobile.billing.domain.ports.in.CreateUserUseCase;
import com.mobile.billing.domain.ports.out.PasswordHashPort;
import com.mobile.billing.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreateUserService implements CreateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordHashPort passwordHash;

    public CreateUserService(UserRepositoryPort userRepository, PasswordHashPort passwordHash) {
        this.userRepository = userRepository;
        this.passwordHash = passwordHash;
    }

    @Override
    public User execute(CreateUserCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }
        User user = User.create(command.tenantId(), command.username(), passwordHash.hash(command.password()), command.roles());
        return userRepository.save(user);
    }
}