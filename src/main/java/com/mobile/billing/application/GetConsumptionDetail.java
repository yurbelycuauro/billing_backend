package com.mobile.billing.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.model.ConsumptionDetailSummary;
import com.mobile.billing.domain.ports.in.GetConsumptionDetailUseCase;
import com.mobile.billing.domain.ports.out.ConsumptionDetailRepositoryPort;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort;

@Service
public class GetConsumptionDetail implements GetConsumptionDetailUseCase {
    
    private final ConsumptionDetailRepositoryPort consumptionDetailRepositoryPort;
    private  final CustomerRepositoryPort customerRepositoryPort;

    public GetConsumptionDetail(
            ConsumptionDetailRepositoryPort consumptionDetailRepositoryPort,
            CustomerRepositoryPort customerRepositoryPort) {
        this.consumptionDetailRepositoryPort = consumptionDetailRepositoryPort;
        this.customerRepositoryPort = customerRepositoryPort;
    }

    @Override
    public Optional<ConsumptionDetail> getConsumptionDetailById(Long id) {
        return consumptionDetailRepositoryPort.findById(id);
    }

    @Override
    public List<ConsumptionDetail> getConsumptionDetail(GetConsumptionDetailCommand command) {
        Optional<String> customerName = customerRepositoryPort.findNameById(command.clientId());
        return consumptionDetailRepositoryPort.findByPeriodYearAndPeriodMonthAndClientName(
            command.anioPeriodo(),
            command.mesPeriodo(),
            customerName.orElse(null)
        );
    }

    @Override
    public List<ConsumptionDetailSummary> getSummaryConsumptionDetail(GetConsumptionDetailCommand command) {
         Optional<String> customerName = customerRepositoryPort.findNameById(command.clientId());
        return consumptionDetailRepositoryPort.findSummaryByPeriodYearAndPeriodMonthAndClientName(
            command.anioPeriodo(),
            command.mesPeriodo(),
            customerName.orElse(null)
        );
    }

    

}
