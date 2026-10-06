package com.mobile.billing.application;

import com.mobile.billing.domain.model.User;
import com.mobile.billing.domain.ports.in.UpdateUserUseCase;
import com.mobile.billing.domain.ports.out.PasswordHashPort;
import com.mobile.billing.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserService implements UpdateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordHashPort passwordHash;

    public UpdateUserService(UserRepositoryPort userRepository, PasswordHashPort passwordHash) {
        this.userRepository = userRepository;
        this.passwordHash = passwordHash;
    }

    @Override
    public User execute(Long id, UpdateUserCommand command) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        String hashedPassword = command.password() == null ? null : passwordHash.hash(command.password());
        user.update(hashedPassword, command.roles());
        if (Boolean.FALSE.equals(command.active())) {
            user.deactivate();
        } else if (Boolean.TRUE.equals(command.active())) {
            user.activate();
        }
        return userRepository.save(user);
    }
}