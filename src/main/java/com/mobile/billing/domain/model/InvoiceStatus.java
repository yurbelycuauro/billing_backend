package com.mobile.billing.domain.model;

public enum InvoiceStatus {
    DRAFT("DRAFT", "#64748B", "draft"),
    PREFAC_GENERATED("PREFAC_GENERATED", "#8B5CF6", "prefac-generated"),
    SENT_TO_CUSTOMER("SENT_TO_CUSTOMER", "#3B82F6", "sent-to-customer"),
    APPROVED("APROVED", "#10B981", "aproved"),
    REJECTED("REJECTED", "#EF4444", "rejected"),
    INVOICED("INVOICED", "#F59E0B", "invoiced");

    private final String label;
    private final String color;
    private final String type;

    InvoiceStatus(String label, String color, String type) {
        this.label = label;
        this.color = color;
        this.type = type;
    }

    public String label() {
        return label;
    }

    public String color() {
        return color;
    }

    public String type() {
        return type;
    }
}
