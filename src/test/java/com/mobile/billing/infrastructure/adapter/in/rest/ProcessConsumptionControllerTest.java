package com.mobile.billing.infrastructure.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mobile.billing.domain.ports.in.GetConsumptionDetailUseCase;
import com.mobile.billing.domain.ports.in.ProcessConsumptionUseCase;

class ProcessConsumptionControllerTest {

    @Test
    void shouldReturnAcceptedWhenDatabaseProcessingIsSubmitted() {
        ProcessConsumptionUseCase processConsumptionUseCase = mock(ProcessConsumptionUseCase.class);
        GetConsumptionDetailUseCase getConsumptionDetailUseCase = mock(GetConsumptionDetailUseCase.class);
        when(processConsumptionUseCase.processConsumption(any()))
                .thenReturn(CompletableFuture.completedFuture(null));
        ProcessConsumptionController controller = new ProcessConsumptionController(
                processConsumptionUseCase,
                getConsumptionDetailUseCase);

        ResponseEntity<String> response = controller.processDatabaseConsumption(2026, 11);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Procesamiento de consumo desde la base de datos aceptado.", response.getBody());
        verify(processConsumptionUseCase).processConsumption(any());
    }
}
