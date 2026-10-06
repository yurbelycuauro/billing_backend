package com.mobile.billing.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.model.Tenant;
import com.mobile.billing.domain.ports.in.CreateCustomerUseCase;
import com.mobile.billing.domain.ports.in.ProcessConsumptionUseCase;
import com.mobile.billing.domain.ports.out.ConsumptionDetailRepositoryPort;
import com.mobile.billing.domain.ports.out.ConsumptionExtractorPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;
import com.mobile.billing.domain.ports.out.TenantRepositoryPort;

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
            CreateCustomerUseCase createCustomerUseCase,
            TenantContextPort tenantContext,
            TenantRepositoryPort tenantRepository) {
        this.consumptionExtractorPort = consumptionExtractorPort;
        this.consumptionDetailRepositoryPort = consumptionDetailRepositoryPort;
        this.createCustomerUseCase = createCustomerUseCase;
        this.tenantContext = tenantContext;
        this.tenantRepository = tenantRepository;
    }

    private final TenantContextPort tenantContext;
    private final TenantRepositoryPort tenantRepository;

    @Override
    @Transactional
    public void processConsumption(ProcessConsumptionCommand command) {
        Long tenantId = tenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context is required to process consumption.");
        }
        log.info("Iniciando procesamiento de consumos para año: {} y mes: {}",
                command.anioPeriodo(), command.mesPeriodo());
        List<ConsumptionDetail> consumptionDetails = consumptionExtractorPort.extraerConsumptions(
                command.dataSource(),
                command.anioPeriodo(),
                command.mesPeriodo(),
                command.excelBytes());
        log.debug("paso");
        for (ConsumptionDetail detail : consumptionDetails) {

            if (detail.getClientName() != null && !detail.getClientName().isBlank()) {
                createCustomerUseCase.createCustomer(
                        new CreateCustomerUseCase.CreateCustomerCommand(
                                detail.getClientName(),
                                "no-reply@billing.local",
                                null));
            }
        }

        consumptionDetailRepositoryPort.saveAll(consumptionDetails);
        log.info("Iniciando procesamiento de consumos para año: {} y mes: {}",
                command.anioPeriodo(), command.mesPeriodo());
    }
}