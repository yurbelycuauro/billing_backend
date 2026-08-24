package com.mobile.billing.infrastructure.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.model.DataSource;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.ConsumptionDetailEntity;

class ConsumptionDetailJpaRepositoryAdapterTest {

    @Test
    void saveAllShouldIgnoreDuplicatesWithinTheSameBatch() {
        SpringDataConsumptionDetailRepository repository = mock(SpringDataConsumptionDetailRepository.class);
        ConsumptionDetailJpaRepositoryAdapter adapter = new ConsumptionDetailJpaRepositoryAdapter(repository);

        ConsumptionDetail detail = new ConsumptionDetail(
            null,
            2024,
            1,
            "Colombia",
            "CC-1",
            "Cliente A",
            "Servicio A",
            "Comercial A",
            "Dirección A",
            "Tipo A",
            "Marca A",
            "Proveedor A",
            10,
            BigDecimal.TEN,
            BigDecimal.TEN,
            BigDecimal.valueOf(100),
            BigDecimal.TEN,
            BigDecimal.TEN,
            BigDecimal.valueOf(90),
            BigDecimal.valueOf(10),
            true,
            null,
            DataSource.MANUAL_EXCEL
        );

        when(repository.existsByPeriodYearAndPeriodMonthAndClientNameAndServiceNameAndCommercialNameAndDirectionAndTypeAndMarkingAndProviderAndSaleAmount(
            2024,
            1,
            "Cliente A",
            "Servicio A",
            "Comercial A",
            "Dirección A",
            "Tipo A",
            "Marca A",
            "Proveedor A",
            BigDecimal.valueOf(100)
        )).thenReturn(false);

        when(repository.saveAll(anyList())).thenAnswer(invocation -> {
            List<ConsumptionDetailEntity> entities = invocation.getArgument(0);
            return entities;
        });

        List<ConsumptionDetail> result = adapter.saveAll(List.of(detail, detail));

        assertEquals(1, result.size());
        verify(repository).saveAll(argThat(entities -> ((List<ConsumptionDetailEntity>) entities).size() == 1));
    }
}
