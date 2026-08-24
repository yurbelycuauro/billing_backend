package com.mobile.billing.domain.model;

import lombok.Getter;

@Getter
public class Payment {
    private final Long id;
    private String paymentMethod;
    private Integer termsDays;

    public Payment(Long id, String paymentMethod, Integer termsDays) {
        this.id = id;
        this.paymentMethod = paymentMethod;
        this.termsDays = termsDays;
    }

    public static Payment create(String paymentMethod, Integer termsDays) {
        return new Payment(null, paymentMethod, termsDays);
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
