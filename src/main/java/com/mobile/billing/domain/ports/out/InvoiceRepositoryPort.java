package com.mobile.billing.domain.ports.out;

import java.util.List;

import com.mobile.billing.domain.model.Invoice;


import java.util.Optional;

public interface InvoiceRepositoryPort {


    Invoice save(Invoice invoice);

    Optional<Invoice> findById(Long id);

    List<Invoice> findAll();

    List<Invoice> findByPeriodYearAndPeriodMonth(Integer periodYear, Integer periodMonth);

    List<Invoice> findByPeriodYearAndPeriodMonthAndCustomerId(
        Integer periodYear, 
        Integer periodMonth, 
        Long customerId
    );
}
