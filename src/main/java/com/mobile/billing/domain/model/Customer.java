package com.mobile.billing.domain.model;

import lombok.Getter;

@Getter
public class Customer {
    private final Long id;
    private String taxId; // RUC / NIT
    private String businessName;
    private String notificationEmail;
    private boolean active;

    public Customer (Long id, String taxId, String businessName, String notificationEmail, boolean active) {
        // 🔒 VALIDACIONES DE NEGOCIO (Invariantes)
        validateTaxId(taxId);
        validateBusinessName(businessName);
        validateEmail(notificationEmail);

        this.id = id;
        this.taxId = taxId;
        this.businessName = businessName;
        this.notificationEmail = notificationEmail;
        this.active = active;

    }

    public static Customer create(String taxId, String businessName, String notificationEmail) {
        return new Customer(null, taxId, businessName, notificationEmail, true);

    }

    public void update(String taxId, String businessName, String notificationEmail) {
        this.taxId = taxId;
        this.businessName = businessName;
        this.notificationEmail = notificationEmail;

    }

    public void deactivate() {
        this.active = false;

    }

    // --- Métodos Privados de Validación ---
    private void validateTaxId(String taxId) {
        if (taxId == null || taxId.isBlank()) {
            throw new IllegalArgumentException("El RUC/NIT (taxId) es obligatorio.");
        }
    }

    private void validateBusinessName(String businessName) {
        if (businessName == null || businessName.isBlank()) {
            throw new IllegalArgumentException("La Razón Social (businessName) no puede estar vacía.");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("El correo de notificación no es válido.");
        }
    }

    
    


}
