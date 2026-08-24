package com.mobile.billing.infrastructure.adapter.out.consumption;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.mobile.billing.domain.model.ConsumptionDetail;
@Component
public class ExternalDatabaseConsumtionAdapter {
    private final JdbcTemplate externalJdbcTemplate; // O la conexión a la BD secundaría

    public ExternalDatabaseConsumtionAdapter(JdbcTemplate externalJdbcTemplate) {
        this.externalJdbcTemplate = externalJdbcTemplate;
    }

    public List<ConsumptionDetail> fetchFromExternalDb(Integer anio, Integer mes) {
        // Query SQL a la base de datos externa/legacy
        return List.of();
    }

}
