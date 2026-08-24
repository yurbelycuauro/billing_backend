package com.mobile.billing.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.model.ConsumptionDetailSummary;

public interface ConsumptionDetailRepositoryPort {

    ConsumptionDetail save(ConsumptionDetail consumptionDetail);
    List<ConsumptionDetail> saveAll(List<ConsumptionDetail> consumptionDetails);
    void deleteById(Long id);
    Optional<ConsumptionDetail> findById(Long id);
    List<ConsumptionDetail> findAll();
    List<ConsumptionDetail> findByPeriodYearAndPeriodMonth(Integer periodYear, Integer periodMonth);
    List<ConsumptionDetail> findByPeriodYearAndPeriodMonthAndClientName(Integer periodYear, Integer periodMonth, String clientName);
    List<ConsumptionDetailSummary> findSummaryByPeriodYearAndPeriodMonthAndClientName(Integer periodYear, Integer periodMonth, String clientName);
    
}
