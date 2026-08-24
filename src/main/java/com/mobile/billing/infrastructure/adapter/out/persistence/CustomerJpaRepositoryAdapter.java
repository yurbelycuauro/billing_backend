package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.CustomerEntity;

@Repository
public class CustomerJpaRepositoryAdapter implements CustomerRepositoryPort {

    private final SpringDataCustomerRepository repository;

    public CustomerJpaRepositoryAdapter(SpringDataCustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = repository.save(CustomerEntity.fromDomain(customer));
        return entity.toDomain();
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return repository.findById(id).map(CustomerEntity::toDomain);
    }

    @Override
    public Optional<Customer> findByTaxId(String taxId) {
        return repository.findByTaxId(taxId).map(CustomerEntity::toDomain);
    }

    @Override
    public Optional<String> findNameById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return repository.findById(id)
                .map(CustomerEntity::getBusinessName);
    }

    @Override
    public Optional<Customer> findByBusinessName(String businessName) {
        if (businessName == null || businessName.isBlank()) {
            return Optional.empty();
        }

        return repository.findByBusinessName(businessName).map(CustomerEntity::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll().stream()
                .map(CustomerEntity::toDomain)
                .toList();
    }
}
