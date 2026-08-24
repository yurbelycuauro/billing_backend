package com.mobile.billing.infrastructure.adapter.in.rest.dto;

public record ConsumptionDetailRequest(
    Long clientId,
    Integer anioPeriodo,
    Integer mesPeriodo
) {

}
