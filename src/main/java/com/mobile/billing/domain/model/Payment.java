package com.mobile.billing.domain.model;

import lombok.Getter;

@Getter
public class Payment {
    private final Long id;
    private Long tenantId;
    private String paymentMethod;
    private Integer termsDays;

    public Payment(Long id, String paymentMethod, Integer termsDays) {
        this(id, null, paymentMethod, termsDays);
    }

    public Payment(Long id, Long tenantId, String paymentMethod, Integer termsDays) {
        this.id = id;
        this.tenantId = tenantId;
        this.paymentMethod = paymentMethod;
        this.termsDays = termsDays;
    }

    public static Payment create(String paymentMethod, Integer termsDays) {
        return new Payment(null, paymentMethod, termsDays);
    }

    public static Payment create(Long tenantId, String paymentMethod, Integer termsDays) {
        return new Payment(null, tenantId, paymentMethod, termsDays);
    }

    public void update(String paymentMethod, Integer termsDays) {
        this.paymentMethod = paymentMethod;
        this.termsDays = termsDays;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Integer getTermsDays() {
        return termsDays;
    }

}
