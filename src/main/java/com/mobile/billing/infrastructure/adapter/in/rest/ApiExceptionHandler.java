package com.mobile.billing.infrastructure.adapter.in.rest;

import com.mobile.billing.domain.exception.CurrencyNotFoundException;
import com.mobile.billing.domain.exception.CustomerNotFoundException;
import com.mobile.billing.domain.exception.DefaultTenantCurrencyException;
import com.mobile.billing.domain.exception.DuplicateCustomerBusinessNameException;
import com.mobile.billing.domain.exception.DuplicateCustomerTaxIdException;
import com.mobile.billing.domain.exception.DuplicateExchangeRateException;
import com.mobile.billing.domain.exception.ExchangeRateNotFoundException;
import com.mobile.billing.domain.exception.InvalidTaxIdException;
import com.mobile.billing.domain.exception.InvalidCurrencyCodeException;
import com.mobile.billing.domain.exception.InvalidExchangeRateException;
import com.mobile.billing.domain.exception.TenantCurrencyNotFoundException;
import com.mobile.billing.domain.exception.TenantCurrencyNotAllowedException;
import com.mobile.billing.domain.exception.UnsupportedTaxIdCountryException;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({CurrencyNotFoundException.class, ExchangeRateNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleNotFound(RuntimeException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomerNotFound(CustomerNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(TenantCurrencyNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTenantCurrencyNotFound(
            TenantCurrencyNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateExchangeRateException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateExchangeRateException exception) {
        return response(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(DuplicateCustomerTaxIdException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateCustomerTaxId(DuplicateCustomerTaxIdException exception) {
        return response(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(DuplicateCustomerBusinessNameException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateCustomerBusinessName(
            DuplicateCustomerBusinessNameException exception) {
        return response(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(DefaultTenantCurrencyException.class)
    public ResponseEntity<ApiErrorResponse> handleDefaultTenantCurrency(
            DefaultTenantCurrencyException exception) {
        return response(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(TenantCurrencyNotAllowedException.class)
    public ResponseEntity<ApiErrorResponse> handleTenantCurrencyNotAllowed(
            TenantCurrencyNotAllowedException exception) {
        return response(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler({
            InvalidCurrencyCodeException.class,
            InvalidExchangeRateException.class,
            InvalidTaxIdException.class,
            UnsupportedTaxIdCountryException.class
    })
    public ResponseEntity<ApiErrorResponse> handleInvalidRequest(RuntimeException exception) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .distinct()
                .collect(java.util.stream.Collectors.joining("; "));
        return response(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(status.getReasonPhrase(), message));
    }
}
