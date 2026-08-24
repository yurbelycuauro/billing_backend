package com.mobile.billing.infrastructure.adapter.out.persistence.mapper;

import com.mobile.billing.domain.model.ConsumptionDetailSummary;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.ConsumptionDetailEntity;

public class ConsumptionMapper {

    public static ConsumptionDetailSummary toConsumptionDetailSummary(ConsumptionDetailEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ConsumptionDetailSummary(
                entity.getId(),
                entity.getServiceName(),
                entity.getTraffic(),
                entity.getSalePrice()
                
                
        );
    }

}
