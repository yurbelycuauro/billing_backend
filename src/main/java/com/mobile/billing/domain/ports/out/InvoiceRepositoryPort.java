package com.mobile.billing.domain.ports.out;

import java.util.List;

import com.mobile.billing.domain.model.Invoice;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceRepositoryPort {


    Invoice save(Invoice invoice);

    Optional<Invoice> findById(Long id);

    List<Invoice> findAll();

    Page<Invoice> findByPeriodYearAndPeriodMonth(Integer periodYear, Integer periodMonth, Pageable pageable);

    Page<Invoice> findByPeriodYearAndPeriodMonthAndCustomerId(
        Integer periodYear, 
        Integer periodMonth, 
        Long customerId, 
        Pageable pageable
    );

    Page<Invoice> findAll(Integer year, Pageable pageable);
}
