package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import java.util.List;

import com.mobile.billing.domain.model.InvoiceItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InvoiceRequest
    (Long clientId,
    Integer anioPeriodo,
    Integer mesPeriodo,
    Long paymentId,
    @NotBlank
    @Size(min = 3, max = 3)
    @Pattern(regexp = "[A-Za-z]{3}")
    String moneda,
    List<InvoiceItem> items
) {

}
