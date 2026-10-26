package com.mobile.billing.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerDto(
                @NotBlank(message = "El taxId (RUC/NIT) no puede estar vacío") @Size(max = 30, message = "El taxId no puede superar los 30 caracteres") String taxId,

                @NotBlank(message = "El nombre de la empresa no puede estar vacío") @Size(max = 150, message = "El nombre de la empresa no puede superar los 150 caracteres") String businessName,

                @NotBlank(message = "El correo de notificación no puede estar vacío") @Email(message = "Debe proporcionar un formato de correo electrónico válido") String notificationEmail,

                boolean active) {
}
