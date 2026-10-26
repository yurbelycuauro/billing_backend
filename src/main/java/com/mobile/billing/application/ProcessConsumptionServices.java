package com.mobile.billing.application;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.model.Customer;
import com.mobile.billing.domain.ports.in.ProcessConsumptionUseCase;
import com.mobile.billing.domain.ports.out.CustomerRepositoryPort;
import com.mobile.billing.domain.ports.out.ConsumptionDetailRepositoryPort;
import com.mobile.billing.domain.ports.out.ConsumptionExtractorPort;
import com.mobile.billing.domain.ports.out.TenantContextPort;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcessConsumptionServices implements ProcessConsumptionUseCase {

    private final ConsumptionExtractorPort consumptionExtractorPort;
    private final ConsumptionDetailRepositoryPort consumptionDetailRepositoryPort;
    private final CustomerRepositoryPort customerRepositoryPort;
    private final TenantContextPort tenantContext;

    public ProcessConsumptionServices(
            ConsumptionExtractorPort consumptionExtractorPort,
            ConsumptionDetailRepositoryPort consumptionDetailRepositoryPort,
            CustomerRepositoryPort customerRepositoryPort,
            TenantContextPort tenantContext) {
        this.consumptionExtractorPort = consumptionExtractorPort;
        this.consumptionDetailRepositoryPort = consumptionDetailRepositoryPort;
        this.customerRepositoryPort = customerRepositoryPort;
        this.tenantContext = tenantContext;
    }

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
        registerMissingCustomers(consumptionDetails, tenantId);
        consumptionDetailRepositoryPort.saveAll(consumptionDetails);
        log.info("Finalizó el procesamiento de consumos para año: {} y mes: {}",
                command.anioPeriodo(), command.mesPeriodo());
    }

    private void registerMissingCustomers(List<ConsumptionDetail> consumptionDetails, Long tenantId) {
        Set<String> processedBusinessNames = new HashSet<>();
        for (ConsumptionDetail detail : consumptionDetails) {
            String businessName = detail.getClientName();
            if (businessName == null || businessName.isBlank()) {
                continue;
            }

            businessName = businessName.trim();
            String normalizedBusinessName = businessName.toLowerCase(Locale.ROOT);
            if (!processedBusinessNames.add(normalizedBusinessName)
                    || customerRepositoryPort.findByTenantIdAndBusinessName(tenantId, businessName).isPresent()) {
                continue;
            }

            customerRepositoryPort.save(Customer.create(
                    tenantId,
                    null,
                    businessName,
                    "no-reply@billing.local"));
        }
    }
}