package com.mobile.billing.domain.model;

import java.time.LocalDateTime;

public class Consumption {
    private String id;
    private String fileName;
    private String storageUri; // Ej: gs://mi-billing-api-storage-2026/temp/archivo.xlsx
    private ConsumptionStatus status; // PENDING, PROCESSING, COMPLETED, FAILED
    private LocalDateTime uploadedAt;

    // Constructor, reglas de negocio y métodos de dominio
    public void markAsProcessing() {
        this.status = ConsumptionStatus.PROCESSING;
    }

    public void markAsCompleted() {
        this.status = ConsumptionStatus.COMPLETED;
    }

    // Getters
    public String getStorageUri() {
        return storageUri;
    }

    public ConsumptionStatus getStatus() {
        return status;
    }
}
