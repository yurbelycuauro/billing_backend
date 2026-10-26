package com.mobile.billing.domain.ports.in;

import java.util.List;

import com.mobile.billing.domain.model.InvoiceItem;

public interface GenerateInvoicePdfUseCase {

    byte[] generateInvoicePdf(GenerateInvoicePdfCommand command);

   record GenerateInvoicePdfCommand(
        Long clientId,
        Integer anioPeriodo,
        Integer mesPeriodo,
        Long paymentId,
        String moneda,
        List<InvoiceItem> items
    ) {
        public GenerateInvoicePdfCommand(Long clientId, Integer anioPeriodo, Integer mesPeriodo,
                Long paymentId, String moneda) {
            this(clientId, anioPeriodo, mesPeriodo, paymentId, moneda, null);
        }
    }

}
