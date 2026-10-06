package com.mobile.billing.infrastructure.adapter.out.persistence;

import com.mobile.billing.domain.model.User;
import com.mobile.billing.domain.ports.out.UserRepositoryPort;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserJpaRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository repository;

    public UserJpaRepositoryAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        return repository.save(UserEntity.fromDomain(user)).toDomain();
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username).map(UserEntity::toDomain);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll().stream().map(UserEntity::toDomain).toList();
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}