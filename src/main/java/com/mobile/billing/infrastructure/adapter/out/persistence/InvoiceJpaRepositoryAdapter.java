package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.mobile.billing.domain.model.Invoice;
import com.mobile.billing.domain.ports.out.InvoiceRepositoryPort;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.CustomerEntity;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.InvoiceEntity;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.PaymentEntity;

@Repository
public class InvoiceJpaRepositoryAdapter implements InvoiceRepositoryPort {

    private final SpringDataInvoiceRepository invoiceRepository;
    private final SpringDataCustomerRepository customerRepository;
    private final SpringDataPaymentRepository paymentRepository;

    public InvoiceJpaRepositoryAdapter(
            SpringDataInvoiceRepository invoiceRepository,
            SpringDataCustomerRepository customerRepository,
            SpringDataPaymentRepository paymentRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Invoice save(Invoice invoice) {
        Long customerId = invoice.getCustomerId();
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalStateException("Customer not found: " + customerId));
        
        Long paymentId = invoice.getPaymentId();
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalStateException("Payment not found: " + paymentId));
       

        InvoiceEntity saved = invoiceRepository.save(InvoiceEntity.fromDomain(invoice, customer,payment));
        return saved.toDomain();
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id).map(InvoiceEntity::toDomain);
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll().stream()
                .map(InvoiceEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invoice> findByPeriodYearAndPeriodMonth(Integer periodYear, Integer periodMonth) {
        LocalDateTime start = LocalDateTime.of(periodYear, periodMonth, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        return invoiceRepository.findByIssueDateBetween(start, end).stream()
                .map(InvoiceEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invoice> findByPeriodYearAndPeriodMonthAndCustomerId(
            Integer periodYear, Integer periodMonth, Long customerId
    ) {
        LocalDateTime start = LocalDateTime.of(periodYear, periodMonth, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        return invoiceRepository.findByCustomer_IdAndIssueDateBetween(customerId, start, end).stream()
                .map(InvoiceEntity::toDomain)
                .collect(Collectors.toList());
    }
}
