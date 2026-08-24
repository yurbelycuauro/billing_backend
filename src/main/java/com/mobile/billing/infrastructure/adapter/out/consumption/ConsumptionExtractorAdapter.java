package com.mobile.billing.infrastructure.adapter.out.consumption;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.model.DataSource;
import com.mobile.billing.domain.ports.out.ConsumptionExtractorPort;

@Component
public class ConsumptionExtractorAdapter implements ConsumptionExtractorPort {

    private final ExcelConsumtionAdapter excelConsumtionAdapter;
    private final ExternalDatabaseConsumtionAdapter externalDatabaseConsumtionAdapter;

    public ConsumptionExtractorAdapter(
            ExcelConsumtionAdapter excelConsumtionAdapter,
            ExternalDatabaseConsumtionAdapter externalDatabaseConsumtionAdapter
    ) {
        this.excelConsumtionAdapter = excelConsumtionAdapter;
        this.externalDatabaseConsumtionAdapter = externalDatabaseConsumtionAdapter;
    }

    @Override
    public List<ConsumptionDetail> extraerConsumptions(DataSource dataSource, Integer anioPeriodo, Integer mesPeriodo, byte[] excelBytes) {
        return switch (dataSource) {
            case MANUAL_EXCEL -> excelConsumtionAdapter.readFromExcel(anioPeriodo, mesPeriodo, excelBytes);
            case DIRECT_DB -> externalDatabaseConsumtionAdapter.fetchFromExternalDb(anioPeriodo, mesPeriodo);
        };
    }
}
