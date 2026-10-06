package com.mobile.billing.application;

import com.mobile.billing.domain.model.User;
import com.mobile.billing.domain.ports.in.GetUserUseCase;
import com.mobile.billing.domain.ports.out.UserRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class GetUserService implements GetUserUseCase {

    private final UserRepositoryPort userRepository;

    public GetUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}