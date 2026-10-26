package com.mobile.billing.infrastructure.adapter.out.persistence;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.CurrencyEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCurrencyRepository extends JpaRepository<CurrencyEntity, String> {

    List<CurrencyEntity> findByActiveTrueOrderByCodeAsc();

    Optional<CurrencyEntity> findByCodeAndActiveTrue(String code);
}
