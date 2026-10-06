package com.mobile.billing.domain.ports.in;

import com.mobile.billing.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface GetUserUseCase {
    List<User> findAll();
    Optional<User> findById(Long id);
}