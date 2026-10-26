package com.mobile.billing.domain.ports.in;

import java.util.List;

import com.mobile.billing.domain.model.Invoice;
import com.mobile.billing.domain.model.InvoiceItem;

public interface CreateInvoiceUseCase {

    Invoice generateInvoice(GenerateInvoiceCommand command);

    record GenerateInvoiceCommand(
        Long clientId,
        Integer anioPeriodo,
        Integer mesPeriodo,
        Long paymentId,
        String moneda,
        List<InvoiceItem> items
    ) {
        public GenerateInvoiceCommand(Long clientId, Integer anioPeriodo, Integer mesPeriodo,
                Long paymentId, String moneda) {
            this(clientId, anioPeriodo, mesPeriodo, paymentId, moneda, null);
        }
    }



}
