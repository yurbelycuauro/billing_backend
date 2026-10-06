package com.mobile.billing.application;

import com.mobile.billing.domain.ports.in.DeleteUserUseCase;
import com.mobile.billing.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserService implements DeleteUserUseCase {

    private final UserRepositoryPort userRepository;

    public DeleteUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }
        userRepository.deleteById(id);
    }
}