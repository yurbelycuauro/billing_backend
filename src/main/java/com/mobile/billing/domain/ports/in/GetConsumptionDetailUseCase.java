package com.mobile.billing.domain.ports.in;

import java.util.List;
import java.util.Optional;

import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.model.ConsumptionDetailSummary;

public interface GetConsumptionDetailUseCase {
    
    Optional<ConsumptionDetail> getConsumptionDetailById(Long id);
    List<ConsumptionDetail> getConsumptionDetail(GetConsumptionDetailCommand command);
    List<ConsumptionDetailSummary> getSummaryConsumptionDetail(GetConsumptionDetailCommand command);

    record GetConsumptionDetailCommand(Long clientId, int anioPeriodo, int mesPeriodo) {
    }


    

}
