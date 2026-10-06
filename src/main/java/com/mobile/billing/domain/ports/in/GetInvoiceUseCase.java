package com.mobile.billing.domain.ports.in;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mobile.billing.domain.model.Invoice;

public interface GetInvoiceUseCase {
    
    Invoice getInvoiceById(Long invoiceId);
    Page<Invoice> findByPeriodYearAndPeriodMonth(Integer periodYear, Integer periodMonth, Pageable pageable);
    Page<Invoice> findByPeriodYearAndPeriodMonthAndCustomerId(Integer periodYear, Integer periodMonth, Long customerId, Pageable pageable);
    List<Invoice> getInvoicesByStatus(String status,Pageable pageable);
    Page<Invoice> findAll(Integer year, Pageable pageable);   
}
