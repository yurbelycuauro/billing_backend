package com.mobile.billing.infrastructure.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mobile.billing.domain.exception.DuplicateCustomerBusinessNameException;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.ApiErrorResponse;

class ApiExceptionHandlerTest {

    @Test
    void shouldReturnConflictWhenCustomerBusinessNameAlreadyExists() {
        ApiExceptionHandler handler = new ApiExceptionHandler();
        DuplicateCustomerBusinessNameException exception =
                new DuplicateCustomerBusinessNameException("Beta S.A.", 7L);

        ResponseEntity<ApiErrorResponse> response = handler.handleDuplicateCustomerBusinessName(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflict", response.getBody().error());
        assertEquals("Ya existe un customer con el businessName: Beta S.A. (ID: 7).",
                response.getBody().message());
    }
}
