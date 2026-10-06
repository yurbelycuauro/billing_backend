package com.mobile.billing.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mobile.billing.domain.model.Invoice;
import com.mobile.billing.domain.ports.in.GetInvoiceUseCase;
import com.mobile.billing.domain.ports.out.InvoiceRepositoryPort;

@Service
public class GetInvoiceService implements GetInvoiceUseCase {
    private final InvoiceRepositoryPort invoiceRepository;

    public GetInvoiceService(InvoiceRepositoryPort invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Invoice getInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with id: " + invoiceId));
    }

    @Override
    public Page<Invoice> findByPeriodYearAndPeriodMonth(Integer periodYear, Integer periodMonth, Pageable pageable) {

        return invoiceRepository.findByPeriodYearAndPeriodMonth(periodYear, periodMonth, pageable);
    }

    @Override
    public Page<Invoice> findByPeriodYearAndPeriodMonthAndCustomerId(Integer periodYear, Integer periodMonth,
            Long customerId, Pageable pageable) {
        return invoiceRepository.findByPeriodYearAndPeriodMonthAndCustomerId(periodYear, periodMonth, customerId,
                pageable);
    }

    @Override
    public List<Invoice> getInvoicesByStatus(String status, Pageable pageable) {
        // Implement the logic to retrieve invoices by status using the repository
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    @Override
    public Page<Invoice> findAll(Integer year, Pageable pageable) {
        return invoiceRepository.findAll(year, pageable);
    }

}
