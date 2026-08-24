package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.stereotype.Repository;

import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.model.ConsumptionDetailSummary;
import com.mobile.billing.domain.ports.out.ConsumptionDetailRepositoryPort;

import com.mobile.billing.infrastructure.adapter.out.persistence.entities.ConsumptionDetailEntity;
import com.mobile.billing.infrastructure.adapter.out.persistence.mapper.ConsumptionMapper;

@Repository
public class ConsumptionDetailJpaRepositoryAdapter implements ConsumptionDetailRepositoryPort {
    

    private final SpringDataConsumptionDetailRepository repository;

    public ConsumptionDetailJpaRepositoryAdapter(SpringDataConsumptionDetailRepository repository) {
        this.repository = repository;
    }

    @Override
    public ConsumptionDetail save(ConsumptionDetail consumptionDetail) {
        if (!isNotDuplicate(consumptionDetail)) {
            return consumptionDetail;
        }

        ConsumptionDetailEntity entity = repository.save(ConsumptionDetailEntity.fromDomain(consumptionDetail));
        return entity.toDomain();
    }

    @Override
    public List<ConsumptionDetail> saveAll(List<ConsumptionDetail> consumptionDetails) {
        List<ConsumptionDetailEntity> entitiesToSave = consumptionDetails.stream()
                .filter(this::isNotDuplicate)
                .map(ConsumptionDetailEntity::fromDomain)
                .collect(Collectors.toList());

        if (entitiesToSave.isEmpty()) {
            return List.of();
        }

        return repository.saveAll(entitiesToSave).stream()
                .map(ConsumptionDetailEntity::toDomain)
                .collect(Collectors.toList());
    }

    private boolean isNotDuplicate(ConsumptionDetail detail) {
        if (detail == null) {
            return false;
        }

        Boolean exists = repository.existsByPeriodYearAndPeriodMonthAndClientNameAndServiceNameAndCommercialNameAndDirectionAndTypeAndMarkingAndProviderAndSaleAmount(
            detail.getPeriodYear(),
            detail.getPeriodMonth(),
            detail.getClientName(),
            detail.getServiceName(),
            detail.getCommercialName(),
            detail.getDirection(),
            detail.getType(),
            detail.getMarking(),
            detail.getProvider(),
            detail.getSaleAmount()
        );

       
        return !exists;
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<ConsumptionDetail> findById(Long id) {
        return repository.findById(id).map(ConsumptionDetailEntity::toDomain);
    }

    @Override
    public List<ConsumptionDetail> findAll() {
        return repository.findAll().stream()
                .map(ConsumptionDetailEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsumptionDetail> findByPeriodYearAndPeriodMonth(Integer periodYear, Integer periodMonth) {
        return repository.findByPeriodYearAndPeriodMonth(periodYear, periodMonth).stream()
                .map(ConsumptionDetailEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsumptionDetail> findByPeriodYearAndPeriodMonthAndClientName(
            Integer periodYear, Integer periodMonth, String clientName
    ) {
        return repository.findByPeriodYearAndPeriodMonthAndClientName(periodYear, periodMonth, clientName).stream()
                .map(ConsumptionDetailEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsumptionDetailSummary> findSummaryByPeriodYearAndPeriodMonthAndClientName(Integer periodYear,
            Integer periodMonth, String clientName) {
                return repository.findByPeriodYearAndPeriodMonthAndClientName(periodYear, periodMonth, clientName).stream()
                .map(ConsumptionMapper::toConsumptionDetailSummary)
                .collect(Collectors.toList());
    }

   
}
