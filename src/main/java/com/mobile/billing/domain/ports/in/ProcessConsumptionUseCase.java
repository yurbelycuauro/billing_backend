package com.mobile.billing.domain.ports.in;

import java.util.concurrent.CompletableFuture;

import com.mobile.billing.domain.model.DataSource;

public interface ProcessConsumptionUseCase {

    CompletableFuture<Void> processConsumption(ProcessConsumptionCommand command);

    record ProcessConsumptionCommand(
        DataSource dataSource,
        Integer anioPeriodo,
        Integer mesPeriodo,
        byte[] excelBytes
    ) {
        public ProcessConsumptionCommand {
            if (dataSource == DataSource.MANUAL_EXCEL && (excelBytes == null || excelBytes.length == 0)) {
                throw new IllegalArgumentException("Para origen MANUAL_EXCEL se requiere el archivo de datos.");
            }
            if (anioPeriodo == null || mesPeriodo == null) {
                throw new IllegalArgumentException("El año y mes de período son obligatorios.");
            }
        }
    }

}
