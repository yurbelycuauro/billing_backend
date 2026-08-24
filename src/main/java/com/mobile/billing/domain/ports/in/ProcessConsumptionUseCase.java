package com.mobile.billing.domain.ports.in;

import com.mobile.billing.domain.model.DataSource;

public interface ProcessConsumptionUseCase {

    // deberia recibir el archivo?

    void processConsumption(
        ProcessConsumptionCommand command // Se recibe como DataSource enum

    );

    record ProcessConsumptionCommand(
        DataSource dataSource,
        Integer anioPeriodo,
        Integer mesPeriodo,
        byte[] excelBytes // Opcional: nulo si el DataSource es DIRECTO_BD
    ) {
        // Validación defensiva en el constructor del DTO
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
