package com.mobile.billing.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.ports.in.CreateCustomerUseCase;
import com.mobile.billing.domain.ports.in.ProcessConsumptionUseCase;
import com.mobile.billing.domain.ports.out.ConsumptionDetailRepositoryPort;
import com.mobile.billing.domain.ports.out.ConsumptionExtractorPort;

import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j
public class ProcessConsumptionServices implements ProcessConsumptionUseCase {

    private final ConsumptionExtractorPort consumptionExtractorPort;
    private final ConsumptionDetailRepositoryPort consumptionDetailRepositoryPort;
    private final CreateCustomerUseCase createCustomerUseCase;

     public ProcessConsumptionServices(
            ConsumptionExtractorPort consumptionExtractorPort, 
            ConsumptionDetailRepositoryPort consumptionDetailRepositoryPort,
            CreateCustomerUseCase createCustomerUseCase) {
        this.consumptionExtractorPort = consumptionExtractorPort;
        this.consumptionDetailRepositoryPort = consumptionDetailRepositoryPort;
        this.createCustomerUseCase = createCustomerUseCase;
    }
    
    @Override
    @Transactional
    public void processConsumption(ProcessConsumptionCommand command) {
        log.info("Iniciando procesamiento de consumos para año: {} y mes: {}", 
            command.anioPeriodo(), command.mesPeriodo());
        List<ConsumptionDetail> consumptionDetails = consumptionExtractorPort.extraerConsumptions(
            command.dataSource(),
            command.anioPeriodo(),
            command.mesPeriodo(),
            command.excelBytes()
        );
        log.debug("paso");
        for (ConsumptionDetail detail : consumptionDetails) {
            
            if (detail.getClientName() != null && !detail.getClientName().isBlank()) {
                createCustomerUseCase.createCustomer(
                    new CreateCustomerUseCase.CreateCustomerCommand(
                        detail.getClientName(),
                        detail.getCountry(),
                        "no-reply@billing.local",
                        null,
                        detail.getClientName()
                    )
                );
            }
        }

        consumptionDetailRepositoryPort.saveAll(consumptionDetails);
        log.info("Iniciando procesamiento de consumos para año: {} y mes: {}", 
            command.anioPeriodo(), command.mesPeriodo());
    }
}