package com.mobile.billing.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id).map(InvoiceEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> findAll() {
        return invoiceRepository.findAll().stream()
                .map(InvoiceEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Invoice> findByPeriodYearAndPeriodMonth(Integer periodYear, Integer periodMonth, Pageable pageable) {
        LocalDateTime start = LocalDateTime.of(periodYear, periodMonth, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        return loadInvoices(invoiceRepository.findInvoiceIdsByIssueDateBetween(start, end, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Invoice> findByPeriodYearAndPeriodMonthAndCustomerId(
            Integer periodYear, Integer periodMonth, Long customerId, Pageable pageable
    ) {
        LocalDateTime start = LocalDateTime.of(periodYear, periodMonth, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        return loadInvoices(invoiceRepository.findInvoiceIdsByCustomerAndIssueDateBetween(
                customerId, start, end, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Invoice> findAll(Integer year, Pageable pageable) {
        return loadInvoices(invoiceRepository.findInvoiceIdsByAnioPeriodo(year, pageable));
    }

    private Page<Invoice> loadInvoices(Page<Long> invoiceIds) {
        if (invoiceIds.isEmpty()) {
            return new PageImpl<>(List.of(), invoiceIds.getPageable(), invoiceIds.getTotalElements());
        }

        Map<Long, InvoiceEntity> invoicesById = invoiceRepository
                .findWithItemsByIdIn(invoiceIds.getContent())
                .stream()
                .collect(Collectors.toMap(InvoiceEntity::getId, invoice -> invoice));

        List<Invoice> invoices = invoiceIds.getContent().stream()
                .map(invoicesById::get)
                .map(InvoiceEntity::toDomain)
                .toList();
        return new PageImpl<>(invoices, invoiceIds.getPageable(), invoiceIds.getTotalElements());
    }
}
