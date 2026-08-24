package com.mobile.billing.domain.ports.in;

import java.util.List;

import com.mobile.billing.domain.model.InvoiceItem;
import com.mobile.billing.domain.model.Moneda;
import com.mobile.billing.domain.model.Pais;

public interface GenerateInvoicePdfUseCase {

    byte[] generateInvoicePdf(GenerateInvoicePdfCommand command);

   record GenerateInvoicePdfCommand(
        Long clientId,
        Integer anioPeriodo,
        Integer mesPeriodo,
        Long paymentId,
        Moneda moneda,
        Pais pais,
        List<InvoiceItem> items
    ) {
        public GenerateInvoicePdfCommand(Long clientId, Integer anioPeriodo, Integer mesPeriodo,
                Long paymentId, Moneda moneda, Pais pais) {
            this(clientId, anioPeriodo, mesPeriodo, paymentId, moneda, pais, null);
        }
    }

}
