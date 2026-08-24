package com.mobile.billing.domain.ports.in;

import java.util.List;

import com.mobile.billing.domain.model.Invoice;
import com.mobile.billing.domain.model.InvoiceItem;
import com.mobile.billing.domain.model.Moneda;
import com.mobile.billing.domain.model.Pais;

public interface CreateInvoiceUseCase {

    Invoice generateInvoice(GenerateInvoiceCommand command);

    record GenerateInvoiceCommand(
        Long clientId,
        Integer anioPeriodo,
        Integer mesPeriodo,
        Long paymentId,
        Moneda moneda,
        Pais pais,
        List<InvoiceItem> items
    ) {
        public GenerateInvoiceCommand(Long clientId, Integer anioPeriodo, Integer mesPeriodo,
                Long paymentId, Moneda moneda, Pais pais) {
            this(clientId, anioPeriodo, mesPeriodo, paymentId, moneda, pais, null);
        }
    }



}
